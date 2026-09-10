package com.iomp.investment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.iomp.investment.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
	
	@ManyToOne
	@JoinColumn(name = "security_id")
	private Security security;
	
	private BigDecimal quantity;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	private LocalDateTime transactionDateTime;

}
