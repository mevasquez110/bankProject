package com.nttdata.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.mongodb.DuplicateKeyException;
import com.nttdata.bank.response.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ApiResponse<Object>> handleWebExchangeBindException(WebExchangeBindException ex) {
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

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
		System.out.println("Tipo de excepción: " + ex.getClass().getName());
		ApiResponse<Object> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("An unexpected error occurred");
		response.setData(ex.getMessage());

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
