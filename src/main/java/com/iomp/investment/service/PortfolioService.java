package com.iomp.investment.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iomp.investment.dto.PortfolioRequest;
import com.iomp.investment.dto.PortfolioResponse;
import com.iomp.investment.enums.PortfolioStatus;
import com.iomp.investment.exception.PortfolioNotFoundException;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.repository.PortfolioRepository;

@Service
public class PortfolioService {
	
	private final PortfolioRepository portfolioRepository;
	
	public PortfolioService(PortfolioRepository portfolioRepository) {
		this.portfolioRepository=portfolioRepository;
	}
	

	public String getMessage() {
		return "Investment Application Running via Service Layer";
	}
	
	public PortfolioResponse createPortfolio(PortfolioRequest request) {

	    Portfolio portfolio = requestToEntity(request);
	    
	    Portfolio savedPortfolio = portfolioRepository.save(portfolio);
	    
	    PortfolioResponse response = entityToResponse(savedPortfolio);
	    return response;
	}
	
	public List<PortfolioResponse> getAllPortfolios(){
		
		return portfolioRepository.findAll()
	            .stream()
	            .map(this::entityToResponse)
	            .toList();
		
	}
	
	public PortfolioResponse getPortfolioById(long id) {
		
		Portfolio portfolio = portfolioRepository.findById(id)
		        .orElseThrow(() ->
		                new PortfolioNotFoundException(
		                        "Portfolio not found with id: " + id
		                ));
		
		PortfolioResponse response = entityToResponse(portfolio);

		return response;
	}
	
	public PortfolioResponse updatePortfolio(long id,PortfolioRequest request) {
		
		Portfolio portfolio =  portfolioRepository.findById(id)
		        .orElseThrow(() ->
                new PortfolioNotFoundException(
                        "Portfolio not found with id: " + id
                ));
		
		portfolio.setOwnerName(request.getOwnerName());
		portfolio.setPortfolioName(request.getPortfolioName());
		
		Portfolio updatedPortfolio = portfolioRepository.save(portfolio);

	    PortfolioResponse response = entityToResponse(updatedPortfolio);
	    return response;
	}
	
	public String deletePortfolio(Long id) {
		
		Portfolio portfolio =  portfolioRepository.findById(id)
		        .orElseThrow(() ->
                new PortfolioNotFoundException(
                        "Portfolio not found with id: " + id
                ));
		
		portfolioRepository.deleteById(id);
		return "Portfolio deleted successfully";

	}
	
	private PortfolioResponse entityToResponse(Portfolio portfolio) {
		
		PortfolioResponse response=new PortfolioResponse();
		response.setId(portfolio.getId());
	    response.setPortfolioName(portfolio.getPortfolioName());
	    response.setOwnerName(portfolio.getOwnerName());
	    response.setStatus(portfolio.getStatus().name());
	    response.setCreatedAt(portfolio.getCreatedAt());

	    return response;
	}
		
	private Portfolio requestToEntity(PortfolioRequest request) {
		
		Portfolio portfolio = new Portfolio();
		
		portfolio.setPortfolioName(request.getPortfolioName());
	    portfolio.setOwnerName(request.getOwnerName());
	    portfolio.setStatus(PortfolioStatus.ACTIVE);
	    portfolio.setCreatedAt(LocalDate.now());
	    
	    return portfolio;
		
	}
	

}
