package com.iomp.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iomp.investment.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
