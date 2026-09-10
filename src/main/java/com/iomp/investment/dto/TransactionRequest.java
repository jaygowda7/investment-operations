package com.iomp.investment.dto;

import java.math.BigDecimal;

import com.iomp.investment.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {
	
	@NotNull
	private Long portfolioId;
	@NotNull
	private Long securityId;
	@NotNull
	private TransactionType transactionType;
	@NotNull
	@Positive
	private BigDecimal quantity;

}
