package com.nttdata.bank.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.AccountsAPI;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.service.AccountsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;

@RestController
public class AccountsController implements AccountsAPI {

	@Autowired
	AccountsService accountService;

	@Override
	@Transactional
	@CircuitBreaker(name = "accountsService", fallbackMethod = "fallbackCreateAccount")
	public ApiResponse<AccountResponse> createAccount(AccountRequest accountRequest) {
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.registerAccount(accountRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Account created successfully");
		response.setData(accountResponse);
		return response;
	}

	public ApiResponse<AccountResponse> fallbackCreateAccount(AccountRequest accountRequest, Throwable t) {
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	@Override
	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<BalanceResponse> checkBalance(String accountNumber) {
		ApiResponse<BalanceResponse> response = new ApiResponse<>();
		BalanceResponse balanceResponse = accountService.checkBalance(accountNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Balance retrieved successfully");
		response.setData(balanceResponse);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<List<AccountResponse>> findAllAccounts() {
		ApiResponse<List<AccountResponse>> response = new ApiResponse<>();
		List<AccountResponse> accounts = accountService.findAllAccounts();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Accounts retrieved successfully");
		response.setData(accounts);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<AccountResponse> updateAccount(String accountId) {
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.updateAccount(accountId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account updated successfully");
		response.setData(accountResponse);
		return response;
	}

	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<Void> deleteAccount(String accountId) {
		ApiResponse<Void> response = new ApiResponse<>();
		accountService.deleteAccount(accountId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Account deleted successfully");
		return response;
	}
}
