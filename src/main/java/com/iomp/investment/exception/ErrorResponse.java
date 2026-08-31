package com.iomp.investment.exception;

import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
	 private int status;
	 private String message;
	 private Map<String, String> errors;

}
