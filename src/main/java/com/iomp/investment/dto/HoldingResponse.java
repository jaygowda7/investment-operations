package com.iomp.investment.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HoldingResponse {
	
	private Long id;
	private Long portfolioId;
	private Long securityId;
	private BigDecimal quantity;

}
