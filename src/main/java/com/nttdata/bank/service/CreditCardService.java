package com.nttdata.bank.service;

import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

public interface CreditCardService {

	CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest);

	CreditCardDebtResponse checkDebt(String creditCardId);
}
