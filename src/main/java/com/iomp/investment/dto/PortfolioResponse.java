package com.iomp.investment.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PortfolioResponse {

	private Long id;

    private String portfolioName;

    private String ownerName;

    private String status;

    private LocalDate createdAt;
}
