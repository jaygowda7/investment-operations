package com.iomp.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import com.iomp.investment.enums.AssetType;
import com.iomp.investment.enums.PortfolioStatus;
import com.iomp.investment.model.Holding;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.repository.HoldingRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;

@SpringBootTest
public class HoldingRepositoryIntegrationTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldDetectOptimisticLockingConflict() {

        // --------------------------------------------------
        // 1. Create test Portfolio
        // --------------------------------------------------

        Portfolio portfolio = new Portfolio();

        portfolio.setPortfolioName("Test Portfolio");
        portfolio.setOwnerName("Test Owner");
        portfolio.setStatus(PortfolioStatus.ACTIVE);
        portfolio.setCreatedAt(LocalDate.now());

        portfolio = portfolioRepository.saveAndFlush(portfolio);

        // --------------------------------------------------
        // 2. Create test Security
        // --------------------------------------------------

        Security security = new Security();

        security.setSymbol("TEST");
        security.setName("Test Security");
        security.setAssetType(AssetType.STOCK);

        security = securityRepository.saveAndFlush(security);

        // --------------------------------------------------
        // 3. Create initial Holding
        // --------------------------------------------------

        Holding holding = new Holding();

        holding.setPortfolio(portfolio);
        holding.setSecurity(security);
        holding.setQuantity(new BigDecimal("100"));

        holding = holdingRepository.saveAndFlush(holding);

        Long holdingId = holding.getId();

        // Initial version should be 0
        assertEquals(0L, holding.getVersion());

        // --------------------------------------------------
        // 4. Load Holding A in its own transaction
        // --------------------------------------------------

        Holding holdingA = transactionTemplate.execute(status ->
            holdingRepository.findById(holdingId).orElseThrow()
        );

        // --------------------------------------------------
        // 5. Load Holding B in another transaction
        // --------------------------------------------------

        Holding holdingB = transactionTemplate.execute(status ->
            holdingRepository.findById(holdingId).orElseThrow()
        );

        // A and B should be separate Java objects
        assertNotSame(holdingA, holdingB);

        // Both have the same initial state

        assertEquals(
                0,
                new BigDecimal("100").compareTo(holdingA.getQuantity())
        );

        assertEquals(
                0,
                new BigDecimal("100").compareTo(holdingB.getQuantity())
        );

        assertEquals(0L, holdingA.getVersion());
        assertEquals(0L, holdingB.getVersion());

        // --------------------------------------------------
        // 6. Update Holding A
        // --------------------------------------------------

        transactionTemplate.executeWithoutResult(status -> {

            holdingA.setQuantity(new BigDecimal("150"));

            holdingRepository.saveAndFlush(holdingA);
        });

        // Verify database was updated

        Holding updatedHolding =
                holdingRepository.findById(holdingId).orElseThrow();

        assertEquals(
                0,
                new BigDecimal("150").compareTo(updatedHolding.getQuantity())
        );

        // Version should have incremented
        assertEquals(1L, updatedHolding.getVersion());

        // B is still stale

        assertEquals(
                0,
                new BigDecimal("100").compareTo(holdingB.getQuantity())
        );

        assertEquals(0L, holdingB.getVersion());

        // --------------------------------------------------
        // 7. Try to save stale Holding B
        // --------------------------------------------------

        assertThrows(
        		ObjectOptimisticLockingFailureException.class,
            () -> transactionTemplate.executeWithoutResult(status -> {

                holdingB.setQuantity(new BigDecimal("130"));

                holdingRepository.saveAndFlush(holdingB);
            })
        );

        // --------------------------------------------------
        // 8. Verify database still contains A's update
        // --------------------------------------------------

        Holding finalHolding =
                holdingRepository.findById(holdingId).orElseThrow();

        assertEquals(
                0,
                new BigDecimal("150").compareTo(finalHolding.getQuantity())
        );

        assertEquals(1L, finalHolding.getVersion());
    }
}