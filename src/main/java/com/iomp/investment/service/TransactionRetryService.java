package com.iomp.investment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;

@Service
public class TransactionRetryService {

    private final TransactionProcessorService transactionProcessorService;

    public TransactionRetryService(
            TransactionProcessorService transactionProcessorService) {

        this.transactionProcessorService = transactionProcessorService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionResponse retryTransaction(
            TransactionRequest request,
            String idempotencyKey,
            String requestHash) {

        return transactionProcessorService.processTransaction(
                request,
                idempotencyKey,
                requestHash);
    }
}