package com.iomp.investment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.HoldingRequest;
import com.iomp.investment.dto.HoldingResponse;
import com.iomp.investment.service.HoldingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {
	
	private final HoldingService service;
	public HoldingController(HoldingService service) {
		this.service=service;
	}
	
	@PostMapping
	public HoldingResponse addHolding( @Valid @RequestBody HoldingRequest request) {
		
		return service.addHolding(request);
	}
		

}
