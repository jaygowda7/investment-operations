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

@Service
public class TransactionService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final TransactionRepository transactionRepository;
    private final RequestHashService requestHashService;
    private final TransactionProcessorService transactionProcessorService;
    private final IdempotencyRecoveryService idempotencyRecoveryService;
    private final DatabaseConstraintHelper databaseConstraintHelper;
    private final TransactionRetryService transactionRetryService;

    public TransactionService(
            IdempotencyRecordRepository idempotencyRecordRepository,
            TransactionRepository transactionRepository,
            RequestHashService requestHashService,
            TransactionProcessorService transactionProcessorService,
            IdempotencyRecoveryService idempotencyRecoveryService,
            DatabaseConstraintHelper databaseConstraintHelper,
            TransactionRetryService transactionRetryService) {

        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.transactionRepository = transactionRepository;
        this.requestHashService = requestHashService;
        this.transactionProcessorService = transactionProcessorService;
        this.idempotencyRecoveryService = idempotencyRecoveryService;
        this.databaseConstraintHelper=databaseConstraintHelper;
        this.transactionRetryService=transactionRetryService;
    }

    public TransactionResponse createTransaction(
            TransactionRequest request,
            String idempotencyKey) {

        String requestHash =
                requestHashService.generateHash(request);

        Optional<IdempotencyRecord> existingRecord =
                idempotencyRecordRepository
                        .findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {

            IdempotencyRecord record = existingRecord.get();

            if (!record.getRequestHash().equals(requestHash)) {

                throw new IdempotencyKeyConflictException(
                        "Idempotency-Key has already been used "
                                + "with a different request");
            }

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
            return transactionProcessorService.processTransaction(
                    request,
                    idempotencyKey,
                    requestHash);
        }catch (DataIntegrityViolationException e) {

            if (databaseConstraintHelper.isHoldingUniqueConstraint(e)) {

                return transactionRetryService.retryTransaction(
                        request,
                        idempotencyKey,
                        requestHash);
            }

            if (databaseConstraintHelper.isIdempotencyUniqueConstraint(e)) {

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