package com.iomp.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
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
import com.iomp.investment.model.IdempotencyRecord;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.model.Transaction;
import com.iomp.investment.repository.IdempotencyRecordRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;
import com.iomp.investment.repository.TransactionRepository;
import com.iomp.investment.service.TransactionService;

@SpringBootTest
class TransactionIdempotencyConcurrencyTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Test
    void sameIdempotencyKeyShouldCreateOnlyOneTransaction()
            throws Exception {

        // Create test portfolio
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioName("Concurrency Test Portfolio");
        portfolio.setOwnerName("Test Owner");
        portfolio.setStatus(PortfolioStatus.ACTIVE);

        portfolio = portfolioRepository.save(portfolio);

        // Create test security
        Security security = new Security();
        security.setSymbol("CONC");
        security.setName("Concurrency Security");
        security.setAssetType(AssetType.STOCK);

        security = securityRepository.save(security);

        // First request
        TransactionRequest firstRequest =
                new TransactionRequest();

        firstRequest.setPortfolioId(portfolio.getId());
        firstRequest.setSecurityId(security.getId());
        firstRequest.setTransactionType(TransactionType.BUY);
        firstRequest.setQuantity(new BigDecimal("100"));

        // Second request - separate object, same data
        TransactionRequest secondRequest =
                new TransactionRequest();

        secondRequest.setPortfolioId(portfolio.getId());
        secondRequest.setSecurityId(security.getId());
        secondRequest.setTransactionType(TransactionType.BUY);
        secondRequest.setQuantity(new BigDecimal("100"));

        String idempotencyKey =
                "CONCURRENT-TEST-" + System.currentTimeMillis();

        CountDownLatch startLatch =
                new CountDownLatch(1);

        ExecutorService executorService =
                Executors.newFixedThreadPool(2);

        Future<TransactionResponse> firstRequestResult =
                executorService.submit(() -> {

                    startLatch.await();

                    return transactionService.createTransaction(
                            firstRequest,
                            idempotencyKey);
                });

        Future<TransactionResponse> secondRequestResult =
                executorService.submit(() -> {

                    startLatch.await();

                    return transactionService.createTransaction(
                            secondRequest,
                            idempotencyKey);
                });

        // Start both requests
        startLatch.countDown();

        TransactionResponse firstResponse =
                firstRequestResult.get();

        TransactionResponse secondResponse =
                secondRequestResult.get();

        executorService.shutdown();

        // Both requests should return a response
        assertNotNull(firstResponse);
        assertNotNull(secondResponse);

        // Both requests must return the same transaction
        assertEquals(
                firstResponse.getId(),
                secondResponse.getId());

        // Only one idempotency record should exist
        List<IdempotencyRecord> records =
                idempotencyRecordRepository
                        .findAll()
                        .stream()
                        .filter(record ->
                                record.getIdempotencyKey()
                                        .equals(idempotencyKey))
                        .toList();

        assertEquals(1, records.size());

        // Verify only one transaction was created
        List<Transaction> transactions =
                transactionRepository.findAll()
                        .stream()
                        .filter(transaction ->
                                transaction.getId()
                                        .equals(firstResponse.getId()))
                        .toList();

        assertEquals(1, transactions.size());

        // Verify transaction quantity
        Transaction transaction =
                transactionRepository
                        .findById(firstResponse.getId())
                        .orElseThrow();

        assertEquals(
                0,
                transaction.getQuantity()
                        .compareTo(new BigDecimal("100")));
    }
}