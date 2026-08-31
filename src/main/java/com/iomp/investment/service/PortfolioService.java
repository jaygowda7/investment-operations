package com.iomp.investment.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iomp.investment.dto.PortfolioRequest;
import com.iomp.investment.dto.PortfolioResponse;
import com.iomp.investment.exception.PortfolioNotFoundException;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.PortfolioStatus;
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

	    Portfolio portfolio = new Portfolio();

	    portfolio.setPortfolioName(request.getPortfolioName());
	    portfolio.setOwnerName(request.getOwnerName());
	    portfolio.setStatus(PortfolioStatus.ACTIVE);
	    portfolio.setCreatedAt(LocalDate.now());

	    Portfolio savedPortfolio = portfolioRepository.save(portfolio);

	    PortfolioResponse response = new PortfolioResponse();

	    response.setId(savedPortfolio.getId());
	    response.setPortfolioName(savedPortfolio.getPortfolioName());
	    response.setOwnerName(savedPortfolio.getOwnerName());
	    response.setStatus(savedPortfolio.getStatus().name());
	    response.setCreatedAt(savedPortfolio.getCreatedAt());

	    return response;
	}
	
	public List<PortfolioResponse> getAllPortfolios(){
		
		return portfolioRepository.findAll()
	            .stream()
	            .map(portfolio -> {

	                PortfolioResponse response = new PortfolioResponse();

	                response.setId(portfolio.getId());
	                response.setPortfolioName(portfolio.getPortfolioName());
	                response.setOwnerName(portfolio.getOwnerName());
	                response.setStatus(portfolio.getStatus().name());
	                response.setCreatedAt(portfolio.getCreatedAt());

	                return response;
	            })
	            .toList();
		
	}
	
	public PortfolioResponse getPortfolioById(long id) {
		
		Portfolio port = portfolioRepository.findById(id)
		        .orElseThrow(() ->
		                new PortfolioNotFoundException(
		                        "Portfolio not found with id: " + id
		                ));
		
		PortfolioResponse response = new PortfolioResponse();

		response.setId(port.getId());
		response.setPortfolioName(port.getPortfolioName());
		response.setOwnerName(port.getOwnerName());
		response.setStatus(port.getStatus().name());
		response.setCreatedAt(port.getCreatedAt());

		return response;
	}
	
	

}
