package com.iomp.investment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.PortfolioRequest;
import com.iomp.investment.dto.PortfolioResponse;
import com.iomp.investment.service.PortfolioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
	
	private final PortfolioService portfolioService;
	
	public PortfolioController(PortfolioService portfolioService) {
		this.portfolioService=portfolioService;
	}
	
	@PostMapping
	public ResponseEntity<PortfolioResponse> createPortfolio( @Valid @RequestBody PortfolioRequest request) {
		PortfolioResponse response = portfolioService.createPortfolio(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping("/{id}")
	public PortfolioResponse updatePortfolio(@PathVariable Long id,@Valid @RequestBody PortfolioRequest request) {
		
		return portfolioService.updatePortfolio(id, request);

	}
	
	@GetMapping
	public List<PortfolioResponse> fetchAllPortfolios() {
		
		return portfolioService.getAllPortfolios();
	}
	
	@GetMapping("/{id}")
	public PortfolioResponse fetchPortfolioById(@PathVariable Long id) {
		
		return portfolioService.getPortfolioById(id);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePortfolio(@PathVariable Long id) {
		String response = portfolioService.deletePortfolio(id);
		return ResponseEntity.ok(response);

	}
}
