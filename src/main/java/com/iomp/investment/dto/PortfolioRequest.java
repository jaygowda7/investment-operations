package com.iomp.investment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PortfolioRequest {
		
		@NotBlank
	 	private String portfolioName;
		@NotBlank
	    private String ownerName;
}
