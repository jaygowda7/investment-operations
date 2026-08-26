package com.iomp.investment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.service.PortfolioService;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
	
	private final PortfolioService portfolioService;
	
	public PortfolioController(PortfolioService portfolioService) {
		this.portfolioService=portfolioService;
	}

	
	@GetMapping("/hello")
    public String hello() {
        return portfolioService.getMessage();
    }
}
