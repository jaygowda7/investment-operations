package com.iomp.investment.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class IdempotencyRecord {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true, length = 255)
	    private String idempotencyKey;

	    @Column(nullable = false, length = 64)
	    private String requestHash;

	    @Column(nullable = false)
	    private Long transactionId;
	    
	    @CreationTimestamp
	    @Column(nullable = false, updatable = false)
	    private LocalDateTime createdAt;

}
