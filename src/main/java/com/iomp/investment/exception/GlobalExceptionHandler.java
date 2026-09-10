package com.iomp.investment.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.OptimisticLockException;

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
	
	@ExceptionHandler(SecurityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleSecurityNotFound(
			SecurityNotFoundException ex) {

	    ErrorResponse response = new ErrorResponse();

	    response.setStatus(404);
	    response.setMessage(ex.getMessage());

	    return ResponseEntity
	            .status(404)
	            .body(response);
	}
	
	@ExceptionHandler(InsufficientHoldingException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientHolding(
			InsufficientHoldingException ex) {

	    ErrorResponse response = new ErrorResponse();

	    response.setStatus(400);
	    response.setMessage(ex.getMessage());

	    return ResponseEntity
	            .status(400)
	            .body(response);
	}
	
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse> handleOptimisticLockException(
			ObjectOptimisticLockingFailureException ex) {

		ErrorResponse response = new ErrorResponse();

	    response.setStatus(HttpStatus.CONFLICT.value());
	    response.setMessage("The holding was modified by another request. Please retry the operation.");

	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(response);
	}
	
	@ExceptionHandler(IdempotencyKeyConflictException.class)
	public ResponseEntity<ErrorResponse> handleIdempotencyKeyConflict(
	        IdempotencyKeyConflictException ex) {

	    ErrorResponse error = new ErrorResponse();
	    error.setStatus(HttpStatus.CONFLICT.value());
	    error.setMessage(ex.getMessage());


	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(error);
	}

}
