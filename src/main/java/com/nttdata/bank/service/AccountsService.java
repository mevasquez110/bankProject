package com.nttdata.bank.service;

import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;

public interface AccountsService {

	AccountResponse registerAccount(AccountRequest accountRequest);
	
	BalanceResponse checkBalance(String accountNumber);
}
