package com.nttdata.bank.controller.impl;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;

@RestController
public class CreditCardsController implements CreditCardsAPI {
	
	@Autowired
	CreditCardService creditCardsService;

	@Override
	public ApiResponse<CreditCardResponse> requestCreditCard(@Valid CreditCardRequest creditCardRequest) {
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.requestCreditCard(creditCardRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit card created successfully");
		response.setData(creditCardResponse);
		return response;
	}

	@Override
	public ApiResponse<CreditCardDebtResponse> checkDebt(String creditCardId) {
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebt(creditCardId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Check debt successfully");
		response.setData(creditCardDebtResponse);
		return response;
	}

}
