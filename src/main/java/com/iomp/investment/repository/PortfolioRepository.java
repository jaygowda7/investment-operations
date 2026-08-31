package com.iomp.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iomp.investment.model.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
	
	

}
