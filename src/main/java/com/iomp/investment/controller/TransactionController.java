package com.iomp.investment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iomp.investment.dto.TransactionRequest;
import com.iomp.investment.dto.TransactionResponse;
import com.iomp.investment.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
	
	private final TransactionService service;
	public TransactionController(TransactionService service) {
		this.service=service;
	}
	
	@PostMapping
	public ResponseEntity<TransactionResponse> createTransaction(
	        @Valid @RequestBody TransactionRequest request,
	        @RequestHeader("Idempotency-Key") String idempotencyKey) {

	    TransactionResponse response =
	            service.createTransaction(request, idempotencyKey);

	    return ResponseEntity.ok(response);
	}

}
