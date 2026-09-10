package com.iomp.investment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class HoldingRequest {
	@NotNull
	private Long portfolioId;
	@NotNull
	private Long securityId;
	@NotNull
	@Positive
	private BigDecimal quantity;

}
