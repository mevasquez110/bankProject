package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.ReprogramDebtRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;

public interface CreditService {

	CreditResponse grantCredit(CreditRequest creditRequest);

	CreditDebtResponse checkDebt(String creditId);

	CreditResponse updateReprogramDebt(ReprogramDebtRequest reprogramDebtRequest);

	List<CreditResponse> findAllCredits();

	void deleteCredit(String creditId);
}
