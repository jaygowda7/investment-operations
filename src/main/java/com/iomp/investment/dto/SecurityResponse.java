package com.iomp.investment.dto;

import com.iomp.investment.enums.AssetType;

import lombok.Data;

@Data
public class SecurityResponse {
	
	private Long id;
	private String symbol;
	private String name;
	private AssetType assetType;

}
