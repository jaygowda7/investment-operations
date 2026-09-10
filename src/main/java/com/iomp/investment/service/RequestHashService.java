package com.iomp.investment.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.iomp.investment.dto.TransactionRequest;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class RequestHashService {
	
	 public String generateHash(TransactionRequest request) {

	        String canonicalString =
	                "portfolioId=" + request.getPortfolioId()
	                + "|securityId=" + request.getSecurityId()
	                + "|transactionType=" + request.getTransactionType()
	                + "|quantity=" + request.getQuantity().stripTrailingZeros();

	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");

	            byte[] hashBytes = digest.digest(
	                    canonicalString.getBytes(StandardCharsets.UTF_8)
	            );

	            StringBuilder hash = new StringBuilder();

	            for (byte b : hashBytes) {
	                hash.append(String.format("%02x", b));
	            }

	            return hash.toString();

	        } catch (NoSuchAlgorithmException e) {
	            throw new IllegalStateException("SHA-256 algorithm not available", e);
	        }
	    }
	}