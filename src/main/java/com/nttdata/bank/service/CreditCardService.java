package com.nttdata.bank.service;

import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

public interface CreditCardService {

	CreditCardResponse requestCreditCard(String customerId);

	CreditCardDebtResponse checkDebt(String creditCardId);
}
