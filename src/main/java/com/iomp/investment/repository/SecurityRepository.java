package com.iomp.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iomp.investment.model.Security;

public interface SecurityRepository extends JpaRepository<Security, Long> {

}