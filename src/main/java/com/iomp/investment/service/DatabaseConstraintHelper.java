package com.iomp.investment.service;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DatabaseConstraintHelper {

    private static final String HOLDING_UNIQUE_CONSTRAINT =
            "uk30emgqm7kw77mcxsfxfdkhn01";

    private static final String IDEMPOTENCY_UNIQUE_CONSTRAINT =
            "idempotency_record_idempotency_key_key";

    public boolean isHoldingUniqueConstraint(
            DataIntegrityViolationException exception) {

        return containsConstraint(
                exception,
                HOLDING_UNIQUE_CONSTRAINT);
    }

    public boolean isIdempotencyUniqueConstraint(
            DataIntegrityViolationException exception) {

        return containsConstraint(
                exception,
                IDEMPOTENCY_UNIQUE_CONSTRAINT);
    }

    private boolean containsConstraint(
            DataIntegrityViolationException exception,
            String constraintName) {

        Throwable cause = exception;

        while (cause != null) {

            if (cause instanceof PSQLException
                    && cause.getMessage() != null
                    && cause.getMessage().contains(constraintName)) {

                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}