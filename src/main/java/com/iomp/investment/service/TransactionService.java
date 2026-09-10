package com.iomp.investment.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.exception.IdempotencyKeyConflictException;
import com.iomp.investment.model.IdempotencyRecord;
import com.iomp.investment.model.Transaction;
import com.iomp.investment.repository.IdempotencyRecordRepository;
import com.iomp.investment.repository.TransactionRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransactionService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final TransactionRepository transactionRepository;
    private final RequestHashService requestHashService;
    private final TransactionProcessorService transactionProcessorService;
    private final IdempotencyRecoveryService idempotencyRecoveryService;
    private final DatabaseConstraintHelper databaseConstraintHelper;

    public TransactionService(
            IdempotencyRecordRepository idempotencyRecordRepository,
            TransactionRepository transactionRepository,
            RequestHashService requestHashService,
            TransactionProcessorService transactionProcessorService,
            IdempotencyRecoveryService idempotencyRecoveryService,
            DatabaseConstraintHelper databaseConstraintHelper) {

        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.transactionRepository = transactionRepository;
        this.requestHashService = requestHashService;
        this.transactionProcessorService = transactionProcessorService;
        this.idempotencyRecoveryService = idempotencyRecoveryService;
        this.databaseConstraintHelper = databaseConstraintHelper;
    }

    public TransactionResponse createTransaction(
            TransactionRequest request,
            String idempotencyKey) {
    	
    	log.info(
    		    "Creating transaction: portfolioId={}, securityId={}, type={}",
    		    request.getPortfolioId(),
    		    request.getSecurityId(),
    		    request.getTransactionType()
    		);

        String requestHash =
                requestHashService.generateHash(request);

        Optional<IdempotencyRecord> existingRecord =
                idempotencyRecordRepository
                        .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {

            IdempotencyRecord record = existingRecord.get();

            if (!record.getRequestHash().equals(requestHash)) {
            	
            	log.warn(
                        "Idempotency key conflict detected: idempotencyKey={}",
                        idempotencyKey
                );

                throw new IdempotencyKeyConflictException(
                        "Idempotency-Key has already been used "
                                + "with a different request");
            }
            
            log.info(
                    "Duplicate transaction request detected, returning existing transaction: "
                            + "idempotencyKey={}, transactionId={}",
                    idempotencyKey,
                    record.getTransactionId()
            );

            Transaction transaction =
                    transactionRepository
                            .findById(record.getTransactionId())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Transaction not found "
                                                    + "for Idempotency-Key"));

            return mapToResponse(transaction);
        }

        try {
        	TransactionResponse response =transactionProcessorService.processTransaction(
        	                request,
        	                idempotencyKey,
        	                requestHash);

        	log.info( "Transaction created successfully: transactionId={}, portfolioId={}, securityId={}, type={}",
        	        response.getId(),
        	        response.getPortfolioId(),
        	        response.getSecurityId(),
        	        response.getTransactionType()
        	);

        	return response;
        }catch (DataIntegrityViolationException e) {

        	if (databaseConstraintHelper.isHoldingUniqueConstraint(e)) {

        	    log.warn(
        	            "Concurrent holding creation detected: "
        	                    + "portfolioId={}, securityId={}, retrying transaction",
        	            request.getPortfolioId(),
        	            request.getSecurityId()
        	    );

        	    try {

        	        return transactionProcessorService.processTransaction(
        	                request,
        	                idempotencyKey,
        	                requestHash);

        	    } catch (DataIntegrityViolationException retryException) {

        	        if (databaseConstraintHelper
        	                .isIdempotencyUniqueConstraint(retryException)) {

        	            log.warn(
        	                    "Idempotency conflict detected during holding retry: "
        	                            + "idempotencyKey={}, recovering transaction",
        	                    idempotencyKey
        	            );

        	            return idempotencyRecoveryService
        	                    .recoverTransaction(idempotencyKey);
        	        }

        	        throw retryException;
        	    }
        	}

            if (databaseConstraintHelper.isIdempotencyUniqueConstraint(e)) {
            	
            	log.warn(
                        "Concurrent idempotency record creation detected: "
                                + "idempotencyKey={}, recovering transaction",
                        idempotencyKey
                );

                return idempotencyRecoveryService
                        .recoverTransaction(idempotencyKey);
            }

            throw e;
        }
    }

    private TransactionResponse mapToResponse(
            Transaction transaction) {

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setPortfolioId(transaction.getPortfolio().getId());
        response.setSecurityId(transaction.getSecurity().getId());
        response.setTransactionType(
                transaction.getTransactionType());
        response.setQuantity(transaction.getQuantity());
        response.setTransactionDateTime(
                transaction.getTransactionDateTime());

        return response;
    }
}