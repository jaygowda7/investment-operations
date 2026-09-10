package com.iomp.investment.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.enums.TransactionType;
import com.iomp.investment.exception.InsufficientHoldingException;
import com.iomp.investment.exception.PortfolioNotFoundException;
import com.iomp.investment.exception.SecurityNotFoundException;
import com.iomp.investment.model.Holding;
import com.iomp.investment.model.IdempotencyRecord;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.model.Transaction;
import com.iomp.investment.repository.HoldingRepository;
import com.iomp.investment.repository.IdempotencyRecordRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;
import com.iomp.investment.repository.TransactionRepository;

@Service
public class TransactionProcessorService {

    private final PortfolioRepository portfolioRepository;
    private final SecurityRepository securityRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public TransactionProcessorService(
            PortfolioRepository portfolioRepository,
            SecurityRepository securityRepository,
            HoldingRepository holdingRepository,
            TransactionRepository transactionRepository,
            IdempotencyRecordRepository idempotencyRecordRepository) {

        this.portfolioRepository = portfolioRepository;
        this.securityRepository = securityRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    @Transactional
    public TransactionResponse processTransaction(
            TransactionRequest request,
            String idempotencyKey,
            String requestHash) {

        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() ->
                        new PortfolioNotFoundException(
                                "Portfolio Not Found for ID: "
                                        + request.getPortfolioId()));

        Security security = securityRepository.findById(request.getSecurityId())
                .orElseThrow(() ->
                        new SecurityNotFoundException(
                                "Security Not Found for ID: "
                                        + request.getSecurityId()));

        Optional<Holding> holding =
                holdingRepository.findByPortfolioAndSecurity(
                        portfolio, security);

        if (request.getTransactionType() == TransactionType.BUY) {

            if (holding.isPresent()) {

                Holding existingHolding = holding.get();

                existingHolding.setQuantity(
                        existingHolding.getQuantity()
                                .add(request.getQuantity()));

                holdingRepository.save(existingHolding);

            } else {

                Holding newHolding = new Holding();

                newHolding.setPortfolio(portfolio);
                newHolding.setSecurity(security);
                newHolding.setQuantity(request.getQuantity());

                holdingRepository.save(newHolding);
            }

        } else {

            if (holding.isEmpty()) {
                throw new InsufficientHoldingException(
                        "Insufficient holding for Security ID: "
                                + request.getSecurityId());
            }

            Holding existingHolding = holding.get();

            if (request.getQuantity()
                    .compareTo(existingHolding.getQuantity()) > 0) {

                throw new InsufficientHoldingException(
                        "Insufficient holding quantity");
            }

            existingHolding.setQuantity(
                    existingHolding.getQuantity()
                            .subtract(request.getQuantity()));

            holdingRepository.save(existingHolding);
        }

        Transaction transaction = new Transaction();

        transaction.setPortfolio(portfolio);
        transaction.setSecurity(security);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setQuantity(request.getQuantity());
        transaction.setTransactionDateTime(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        IdempotencyRecord record = new IdempotencyRecord();

        record.setIdempotencyKey(idempotencyKey);
        record.setRequestHash(requestHash);
        record.setTransactionId(transaction.getId());

        idempotencyRecordRepository.saveAndFlush(record);

        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setPortfolioId(transaction.getPortfolio().getId());
        response.setSecurityId(transaction.getSecurity().getId());
        response.setTransactionType(transaction.getTransactionType());
        response.setQuantity(transaction.getQuantity());
        response.setTransactionDateTime(
                transaction.getTransactionDateTime());

        return response;
    }
}