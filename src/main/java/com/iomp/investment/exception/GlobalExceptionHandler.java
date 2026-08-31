package com.iomp.investment.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
	        MethodArgumentNotValidException ex) {
		
		ErrorResponse response=new ErrorResponse();
		
		response.setStatus(400);
		response.setMessage("Validation failed");
		Map<String, String> errors =
		            ex.getBindingResult()
		              .getFieldErrors()
		              .stream()
		              .collect(Collectors.toMap(
		                      error -> error.getField(),
		                      error -> error.getDefaultMessage()
		              ));
		 response.setErrors(errors);
		 
		 return ResponseEntity
			        .badRequest()
			        .body(response);

	}
	
	@ExceptionHandler(PortfolioNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePortfolioNotFound(
	        PortfolioNotFoundException ex) {

	    ErrorResponse response = new ErrorResponse();

	    response.setStatus(404);
	    response.setMessage(ex.getMessage());

	    return ResponseEntity
	            .status(404)
	            .body(response);
	}

}
