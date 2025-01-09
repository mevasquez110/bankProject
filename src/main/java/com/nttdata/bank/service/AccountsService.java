package com.nttdata.bank.service;

import java.util.List;

import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;

public interface AccountsService {

	AccountResponse registerAccount(AccountRequest accountRequest);

	BalanceResponse checkBalance(String accountNumber);

	List<AccountResponse> findAllAccounts();

	AccountResponse updateAccount(String accountId);

	void deleteAccount(String accountId);
}
