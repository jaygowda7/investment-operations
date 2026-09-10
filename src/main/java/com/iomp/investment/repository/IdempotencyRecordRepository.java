package com.iomp.investment.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iomp.investment.model.IdempotencyRecord;

public interface IdempotencyRecordRepository
extends JpaRepository<IdempotencyRecord, Long> {

Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}
