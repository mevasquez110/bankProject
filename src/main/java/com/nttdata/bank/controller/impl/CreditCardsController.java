package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

@RestController
public class CreditCardsController implements CreditCardsAPI {

	@Autowired
	CreditCardService creditCardsService;

	@Override
	@Transactional
	@CircuitBreaker(name = "creditCardsService", fallbackMethod = "fallbackRequestCreditCard")
	public ApiResponse<CreditCardResponse> requestCreditCard(CreditCardRequest creditCardRequest) {
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.requestCreditCard(creditCardRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit card created successfully");
		response.setData(creditCardResponse);
		return response;
	}

	public ApiResponse<CreditCardResponse> fallbackRequestCreditCard(CreditCardRequest creditCardRequest, Throwable t) {
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	@Override
	@Transactional
	@CircuitBreaker(name = "creditCardsService")
	public ApiResponse<CreditCardDebtResponse> checkDebt(String creditCardId) {
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebt(creditCardId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Check debt successfully");
		response.setData(creditCardDebtResponse);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "creditCardsService")
	public ApiResponse<List<CreditCardResponse>> findAllCreditCards() {
		ApiResponse<List<CreditCardResponse>> response = new ApiResponse<>();
		List<CreditCardResponse> creditCards = creditCardsService.findAllCreditCards();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit cards retrieved successfully");
		response.setData(creditCards);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "creditCardsService")
	public ApiResponse<CreditCardResponse> updateCreditCard(String creditCardId) {
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.updateCreditCard(creditCardId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit card updated successfully");
		response.setData(creditCardResponse);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "creditCardsService")
	public ApiResponse<Void> deleteCreditCard(String creditCardId) {
		ApiResponse<Void> response = new ApiResponse<>();
		creditCardsService.deleteCreditCard(creditCardId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Credit card deleted successfully");
		return response;
	}
}
