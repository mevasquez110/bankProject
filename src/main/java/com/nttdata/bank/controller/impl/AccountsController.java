package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
	public ApiResponse<AccountResponse> createSavingsAccount(AccountRequest accountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<AccountResponse> createCurrentAccount(AccountRequest accountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<AccountResponse> createFixedTermAccount(AccountRequest accountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<BalanceResponse> checkBalance(Integer accountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
