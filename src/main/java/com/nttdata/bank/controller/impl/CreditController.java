package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditAPI;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.DebtResponse;
import com.nttdata.bank.service.CreditService;

@RestController
public class CreditController implements CreditAPI {

	@Autowired
	CreditService creditService;

	@Override
	public ApiResponse<CreditResponse> grantPersonalCredit(CreditRequest creditRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<CreditResponse> grantBusinessCredit(CreditRequest creditRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<DebtResponse> checkDebt(int creditId) {
		// TODO Auto-generated method stub
		return null;
	}

}
