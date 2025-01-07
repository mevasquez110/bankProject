package com.nttdata.bank.service;

import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;

public interface CreditService {

	CreditResponse grantCredit(CreditRequest creditRequest);

	CreditDebtResponse checkDebt(String creditId);
}
