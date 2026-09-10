package com.iomp.investment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.model.IdempotencyRecord;
import com.iomp.investment.model.Transaction;
import com.iomp.investment.repository.IdempotencyRecordRepository;
import com.iomp.investment.repository.TransactionRepository;



@Service
public class IdempotencyRecoveryService {
	
	 private final IdempotencyRecordRepository idempotencyRecordRepository;
	    private final TransactionRepository transactionRepository;

	    public IdempotencyRecoveryService(
	            IdempotencyRecordRepository idempotencyRecordRepository,
	            TransactionRepository transactionRepository) {

	        this.idempotencyRecordRepository = idempotencyRecordRepository;
	        this.transactionRepository = transactionRepository;
	    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionResponse recoverTransaction(String idempotencyKey) {

		IdempotencyRecord record = idempotencyRecordRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Idempotency record not found"));

        Transaction transaction = transactionRepository
                .findById(record.getTransactionId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Transaction not found for Idempotency-Key"));

        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setPortfolioId(transaction.getPortfolio().getId());
        response.setSecurityId(transaction.getSecurity().getId());
        response.setTransactionType(transaction.getTransactionType());
        response.setQuantity(transaction.getQuantity());
        response.setTransactionDateTime(transaction.getTransactionDateTime());

        return response;
    }

}
