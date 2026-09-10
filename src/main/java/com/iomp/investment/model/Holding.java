package com.iomp.investment.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"portfolio_id", "security_id"})
	    }
	)
@Setter
@Getter
public class Holding {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "portfolio_id")
	private Portfolio portfolio;
	
	@ManyToOne
	@JoinColumn(name = "security_id")
	private Security security;
	
	private BigDecimal quantity;
	
	@Version
    private Long version;
	

}
