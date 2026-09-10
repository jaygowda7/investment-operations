package com.iomp.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.enums.AssetType;
import com.iomp.investment.enums.PortfolioStatus;
import com.iomp.investment.enums.TransactionType;
import com.iomp.investment.model.Holding;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.repository.HoldingRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class TransactionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private HoldingRepository holdingRepository;
    
    @Test
    void shouldHandleConcurrentTransactionRequests() {

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Concurrency Test Portfolio");
        portfolio.setOwnerName("Test Owner");
        portfolio.setStatus(PortfolioStatus.ACTIVE);
        portfolio.setCreatedAt(LocalDate.now());

        portfolio = portfolioRepository.saveAndFlush(portfolio);


        Security security = new Security();
        security.setSymbol("CONC");
        security.setName("Concurrency Test Security");
        security.setAssetType(AssetType.STOCK);

        security = securityRepository.saveAndFlush(security);


        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setSecurity(security);
        holding.setQuantity(new BigDecimal("100"));

        holding = holdingRepository.saveAndFlush(holding);

        Long portfolioId = portfolio.getId();
        Long securityId = security.getId();
        Long holdingId = holding.getId();

        assertEquals(0L, holding.getVersion());
        TransactionRequest request = new TransactionRequest();

        request.setPortfolioId(portfolioId);
        request.setSecurityId(securityId);
        request.setTransactionType(TransactionType.BUY);
        request.setQuantity(new BigDecimal("50"));
        
        ResponseEntity<TransactionResponse> response = restTemplate.postForEntity(
                "/api/transactions",
                request,
                TransactionResponse.class
        );
        
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(50, response.getBody().getQuantity().intValue());
        
        Holding updatedHolding = holdingRepository.findById(holdingId).orElseThrow();

        System.out.println("Holding Quantity: " + updatedHolding.getQuantity());
        System.out.println("Holding Version: " + updatedHolding.getVersion());

        assertEquals(
                0,
                new BigDecimal("150").compareTo(updatedHolding.getQuantity())
        );

        assertEquals(1L, updatedHolding.getVersion());
    }
    

    
}
