package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

public interface CreditCardService {

	CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest);

	CreditCardDebtResponse checkDebt(String creditCardId);

	List<CreditCardResponse> findAllCreditCards();

	CreditCardResponse updateCreditCard(String creditCardId);

	void deleteCreditCard(String creditCardId);

}
