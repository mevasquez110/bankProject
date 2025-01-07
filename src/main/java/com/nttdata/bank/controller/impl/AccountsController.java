package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.AccountsAPI;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.service.AccountsService;

@RestController
public class AccountsController implements AccountsAPI {

	@Autowired 
	AccountsService accountService;

	@Override
	public ApiResponse<AccountResponse> createAccount(AccountRequest accountRequest) {
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.registerAccount(accountRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Account createded successfully");
		response.setData(accountResponse);
		return response;
	}

	@Override
	public ApiResponse<BalanceResponse> checkBalance(String accountNumber) {
		// TODO Auto-generated method stub
		return null;
	}

}
