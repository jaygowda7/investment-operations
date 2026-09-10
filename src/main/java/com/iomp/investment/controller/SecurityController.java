package com.iomp.investment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.SecurityRequest;
import com.iomp.investment.dto.SecurityResponse;
import com.iomp.investment.service.SecurityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/securities")
public class SecurityController {

    private final SecurityService service;

    public SecurityController(SecurityService service) {
        this.service = service;
    }

    @PostMapping
    public SecurityResponse createSecurity(
            @Valid @RequestBody SecurityRequest request) {

        return service.createSecurity(request);
    }
    
    @GetMapping
    public List<SecurityResponse> fetchSecurities(){
    	
    	return service.getSecurities();
    }
    
    @GetMapping("/{id}")
    public SecurityResponse fetchSecurityById(@PathVariable long id) {
    	
    	return service.getSecurityById(id);
    	
    }
}
