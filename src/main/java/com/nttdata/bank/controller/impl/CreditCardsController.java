package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardsService;

@RestController
public class CreditCardsController implements CreditCardsAPI {

	@Autowired
	CreditCardsService creditCardsService;

	@Override
	public ApiResponse<CreditCardResponse> requestCreditCard(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<CreditCardDebtResponse> checkDebt(String creditCardId) {
		// TODO Auto-generated method stub
		return null;
	}

}
