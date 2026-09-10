package com.iomp.investment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iomp.investment.dto.SecurityRequest;
import com.iomp.investment.dto.SecurityResponse;
import com.iomp.investment.exception.SecurityNotFoundException;
import com.iomp.investment.model.Security;
import com.iomp.investment.repository.SecurityRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SecurityService {

    private final SecurityRepository securityRepository;

    public SecurityService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }
    
    public SecurityResponse createSecurity(SecurityRequest request) {
    	
    	log.info(
                "Creating security: symbol={}, assetType={}",
                request.getSymbol(),
                request.getAssetType()
        );
    	
    	Security security = requestToEntity(request);
    	
    	security = securityRepository.save(security);
    	SecurityResponse response = entityToResponse(security);
    	
    	return response;
    }
    
    public List<SecurityResponse> getSecurities(){
    	
    	log.info("Fetching all securities");
    	 return securityRepository.findAll().
    			 stream().
    			 map(this::entityToResponse).
    			 toList();
    }
    
    public SecurityResponse getSecurityById(long id) {
    	
    	log.info("Fetching security: securityId={}", id);
    	
    	Security security = securityRepository.findById(id).orElseThrow(() ->
                new SecurityNotFoundException(
                        "Security not found with id: " + id
                ));
    	SecurityResponse response = entityToResponse(security);
    	return response;
    }

	private SecurityResponse entityToResponse(Security security) {
		
		SecurityResponse response = new SecurityResponse();
		response.setId(security.getId());
		response.setSymbol(security.getSymbol());
		response.setName(security.getName());
		response.setAssetType(security.getAssetType());
		
		return response;
	}

	private Security requestToEntity(SecurityRequest request) {
		Security security = new Security();
		security.setSymbol(request.getSymbol());
		security.setName(request.getName());
		security.setAssetType(request.getAssetType());
		return security;
	}
}
