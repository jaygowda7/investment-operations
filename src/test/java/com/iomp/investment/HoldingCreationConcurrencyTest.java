package com.iomp.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.enums.AssetType;
import com.iomp.investment.enums.PortfolioStatus;
import com.iomp.investment.enums.TransactionType;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.repository.HoldingRepository;
import com.iomp.investment.repository.IdempotencyRecordRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;
import com.iomp.investment.repository.TransactionRepository;
import com.iomp.investment.service.TransactionService;

@SpringBootTest
public class HoldingCreationConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Test
    void concurrentBuyRequestsShouldHandleHoldingCreation()
            throws Exception {

        // Create test portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Holding Race Portfolio");
        portfolio.setOwnerName("Test Owner");
        portfolio.setStatus(PortfolioStatus.ACTIVE);

        portfolio = portfolioRepository.save(portfolio);

        // Create test security
        Security security = new Security();
        security.setSymbol("RACE");
        security.setName("Race Security");
        security.setAssetType(AssetType.STOCK);

        security = securityRepository.save(security);

        // First BUY request
        TransactionRequest firstRequest =
                new TransactionRequest();

        firstRequest.setPortfolioId(portfolio.getId());
        firstRequest.setSecurityId(security.getId());
        firstRequest.setTransactionType(TransactionType.BUY);
        firstRequest.setQuantity(new BigDecimal("100"));

        // Second BUY request
        TransactionRequest secondRequest =
                new TransactionRequest();

        secondRequest.setPortfolioId(portfolio.getId());
        secondRequest.setSecurityId(security.getId());
        secondRequest.setTransactionType(TransactionType.BUY);
        secondRequest.setQuantity(new BigDecimal("50"));

        // Different idempotency keys
        String firstIdempotencyKey =
                "RACE-TEST-1-" + System.currentTimeMillis();

        String secondIdempotencyKey =
                "RACE-TEST-2-" + System.currentTimeMillis();

        CountDownLatch startLatch =
                new CountDownLatch(1);
        long transactionCountBefore =
                transactionRepository.count();

        long idempotencyRecordCountBefore =
                idempotencyRecordRepository.count();

        ExecutorService executorService =
                Executors.newFixedThreadPool(2);

        Future<TransactionResponse> firstResult =
                executorService.submit(() -> {

                    startLatch.await();

                    return transactionService.createTransaction(
                            firstRequest,
                            firstIdempotencyKey);
                });

        Future<TransactionResponse> secondResult =
                executorService.submit(() -> {

                    startLatch.await();

                    return transactionService.createTransaction(
                            secondRequest,
                            secondIdempotencyKey);
                });

        // Start both requests
        startLatch.countDown();

        TransactionResponse firstResponse =
                firstResult.get();

        TransactionResponse secondResponse =
                secondResult.get();

        executorService.shutdown();

        // --------------------------------------------------
        // Assertions
        // --------------------------------------------------

        // Both requests should succeed
        assertNotNull(firstResponse);
        assertNotNull(secondResponse);

        // There should be exactly one Holding
        var holding = holdingRepository
                .findByPortfolioAndSecurity(portfolio, security)
                .orElseThrow();

        // Final quantity should be 100 + 50 = 150
        assertEquals(
                0,
                holding.getQuantity()
                        .compareTo(new BigDecimal("150"))
        );

        assertEquals(
                transactionCountBefore + 2,
                transactionRepository.count()
        );

        assertEquals(
                idempotencyRecordCountBefore + 2,
                idempotencyRecordRepository.count()
        );
    }
}