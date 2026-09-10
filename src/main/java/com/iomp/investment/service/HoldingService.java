package com.iomp.investment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iomp.investment.dto.HoldingRequest;
import com.iomp.investment.dto.HoldingResponse;
import com.iomp.investment.exception.PortfolioNotFoundException;
import com.iomp.investment.exception.SecurityNotFoundException;
import com.iomp.investment.model.Holding;
import com.iomp.investment.model.Portfolio;
import com.iomp.investment.model.Security;
import com.iomp.investment.repository.HoldingRepository;
import com.iomp.investment.repository.PortfolioRepository;
import com.iomp.investment.repository.SecurityRepository;

@Service
public class HoldingService {
	
	private final PortfolioRepository portfolioRepository;
	private final SecurityRepository securityRepository;
	private final HoldingRepository holdingRepository;
	
	public HoldingService(
			PortfolioRepository portfolioRepository,
			SecurityRepository securityRepository,
			HoldingRepository holdingRepository) {
		this.portfolioRepository=portfolioRepository;
		this.securityRepository=securityRepository;
		this.holdingRepository=holdingRepository;
	}
	
	
	public HoldingResponse addHolding(HoldingRequest holdingRequest) {

	    Portfolio portfolio = portfolioRepository.findById(holdingRequest.getPortfolioId())
	            .orElseThrow(() -> new PortfolioNotFoundException("Portfolio Not Found: "+ holdingRequest.getPortfolioId()));

	    Security security = securityRepository.findById(holdingRequest.getSecurityId())
	            .orElseThrow(() -> new SecurityNotFoundException("Security Not Found: "+ holdingRequest.getSecurityId()));
	    
	    Optional<Holding> existingHolding =
	            holdingRepository.findByPortfolioAndSecurity(portfolio, security);
	    
	    Holding holding;
	    if(existingHolding.isPresent()) {
	    	
	    	holding = existingHolding.get();
		    holding.setQuantity(holding.getQuantity().add(holdingRequest.getQuantity()));
		    
		    holding = holdingRepository.save(holding);
		    
	    }else {
	    	
	    	holding = new Holding();
	    	
	    	holding.setPortfolio(portfolio);
	    	holding.setSecurity(security);
	    	holding.setQuantity(holdingRequest.getQuantity());
	    	
	    	holding = holdingRepository.save(holding); 
	    }
	    
	    return entityToResponse(holding);
	    
	}
	
	private HoldingResponse entityToResponse(Holding holding) {
		
		HoldingResponse response = new HoldingResponse();
		response.setId(holding.getId());
		response.setPortfolioId(holding.getPortfolio().getId());
		response.setSecurityId(holding.getSecurity().getId());
		response.setQuantity(holding.getQuantity());
		
		return response;
		
	}

}
