package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditAPI;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;

@RestController
public class CreditController implements CreditAPI {

	@Autowired
	CreditService creditService;

	@Override
	public ApiResponse<CreditResponse> grantCredit(CreditRequest creditRequest) {
		ApiResponse<CreditResponse> response = new ApiResponse<>();
		CreditResponse creditResponse = creditService.grantCredit(creditRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit granted successfully");
		response.setData(creditResponse);
		return response;
	}

	@Override
	public ApiResponse<CreditDebtResponse> checkDebt(String creditId) {
		ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
		CreditDebtResponse creditDebtResponse = creditService.checkDebt(creditId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Debt retrieved successfully");
		response.setData(creditDebtResponse);
		return response;
	}

}
