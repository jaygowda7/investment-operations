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

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PortfolioService {
	
	private final PortfolioRepository portfolioRepository;
	
	public PortfolioService(PortfolioRepository portfolioRepository) {
		this.portfolioRepository=portfolioRepository;
	}
	

	public String getMessage() {
		return "Investment Application Running via Service Layer";
	}
	
	public PortfolioResponse createPortfolio(PortfolioRequest request) {
		
		log.info("Creating portfolio: portfolioName={}, ownerName={}",
			    request.getPortfolioName(),request.getOwnerName());

	    Portfolio portfolio = requestToEntity(request);
	    
	    Portfolio savedPortfolio = portfolioRepository.save(portfolio);
	    
	    PortfolioResponse response = entityToResponse(savedPortfolio);
	    return response;
	}
	
	public List<PortfolioResponse> getAllPortfolios(){
		
		log.info("Fetching all portfolios");
		
		return portfolioRepository.findAll()
	            .stream()
	            .map(this::entityToResponse)
	            .toList();
		
	}
	
	public PortfolioResponse getPortfolioById(long id) {
		
		log.info("Fetching portfolio: portfolioId={}", id);
		
		Portfolio portfolio = portfolioRepository.findById(id)
		        .orElseThrow(() ->
		                new PortfolioNotFoundException(
		                        "Portfolio not found with id: " + id
		                ));
		
		PortfolioResponse response = entityToResponse(portfolio);

		return response;
	}
	
	public PortfolioResponse updatePortfolio(long id,PortfolioRequest request) {
		
		log.info("Updating portfolio: portfolioId={}", id);
		
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
	
	public void deletePortfolio(Long id) {
		
		log.info("Deleting portfolio: portfolioId={}", id);
		
		Portfolio portfolio =  portfolioRepository.findById(id)
		        .orElseThrow(() ->
                new PortfolioNotFoundException(
                        "Portfolio not found with id: " + id
                ));
		
		portfolioRepository.deleteById(id);

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
