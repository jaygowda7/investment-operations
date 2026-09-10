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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.iomp.investment.dto.PortfolioRequest;
import com.iomp.investment.dto.PortfolioResponse;
import com.iomp.investment.service.PortfolioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/portfolios")
@Tag( name = "Portfolios", description = "APIs for managing investment portfolios" )
public class PortfolioController {
	
	private final PortfolioService portfolioService;
	
	public PortfolioController(PortfolioService portfolioService) {
		this.portfolioService=portfolioService;
	}
	
	@PostMapping
	@Operation( summary = "Create a portfolio", description = "Creates a new investment portfolio" ) 
				@ApiResponses({ @ApiResponse( responseCode = "201", description = "Portfolio created successfully" ), 
				@ApiResponse( responseCode = "400", description = "Invalid portfolio data" ) })
	public ResponseEntity<PortfolioResponse> createPortfolio( @Valid @RequestBody PortfolioRequest request) {
		PortfolioResponse response = portfolioService.createPortfolio(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping("/{id}")
	@Operation( summary = "Update a portfolio", description = "Updates an existing investment portfolio" ) 
				@ApiResponses({ @ApiResponse( responseCode = "200", description = "Portfolio updated successfully" ), 
				@ApiResponse( responseCode = "400", description = "Invalid portfolio data" ), 
				@ApiResponse( responseCode = "404", description = "Portfolio not found" ) })
	public PortfolioResponse updatePortfolio(@PathVariable Long id,@Valid @RequestBody PortfolioRequest request) {
		
		return portfolioService.updatePortfolio(id, request);

	}
	
	@GetMapping
	@Operation( summary = "Get all portfolios", description = "Retrieves all investment portfolios" )
				@ApiResponse( responseCode = "200", description = "Portfolios retrieved successfully" )
	public List<PortfolioResponse> fetchAllPortfolios() {
		
		return portfolioService.getAllPortfolios();
	}
	
	@GetMapping("/{id}")
	@Operation( summary = "Get portfolio by ID", description = "Retrieves a portfolio using its unique ID" ) 
				@ApiResponses({ @ApiResponse( responseCode = "200", description = "Portfolio retrieved successfully" ),
				@ApiResponse( responseCode = "404", description = "Portfolio not found" ) })
	public PortfolioResponse fetchPortfolioById(@PathVariable Long id) {
		
		return portfolioService.getPortfolioById(id);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a portfolio",description = "Deletes an existing investment portfolio")
				@ApiResponses({@ApiResponse(responseCode = "204",description = "Portfolio deleted successfully"),
				@ApiResponse(responseCode = "404",description = "Portfolio not found")})
	public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {

	    portfolioService.deletePortfolio(id);

	    return ResponseEntity.noContent().build();
	}
}
