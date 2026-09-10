package com.iomp.investment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iomp.investment.model.Holding;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
	
	Optional<Holding> findByPortfolioAndSecurity(
            Portfolio portfolio,
            Security security
    );

}