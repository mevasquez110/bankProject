package com.nttdata.bank.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import com.nttdata.bank.response.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handles validation errors (WebExchangeBindException).
	 *
	 * @param ex The WebExchangeBindException instance
	 * @return ResponseEntity containing the ApiResponse with validation errors
	 */
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ApiResponse<Object>> handleWebExchangeBindException(WebExchangeBindException ex) {
		logger.error("Validation failed: {}", ex.getMessage());
		ApiResponse<Object> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setMessage("Validation failed");

		List<Map<String, String>> errorMessages = ex.getFieldErrors().stream().map(fieldError -> {
			Map<String, String> error = new HashMap<>();
			error.put("error", fieldError.getDefaultMessage());
			return error;
		}).collect(Collectors.toList());

		response.setData(errorMessages);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles general exceptions (Exception).
	 *
	 * @param ex The Exception instance
	 * @return ResponseEntity containing the ApiResponse with error message
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
		logger.error("An unexpected error occurred: {}", ex.getMessage());
		ApiResponse<Object> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("An unexpected error occurred");
		response.setData(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
