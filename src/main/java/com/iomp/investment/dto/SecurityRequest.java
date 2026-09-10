package com.iomp.investment.dto;

import com.iomp.investment.enums.AssetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SecurityRequest {
	
	@NotBlank
	private String symbol;
	@NotBlank
	private String name;
	@NotNull
	private AssetType assetType;

}
