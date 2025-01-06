package com.nttdata.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;

@RestController
@RequestMapping("/accounts")
public interface AccountsAPI {

	@PostMapping("/savings")
	ApiResponse<AccountResponse> createSavingsAccount(@RequestBody AccountRequest accountRequest);

	@PostMapping("/current")
	ApiResponse<AccountResponse> createCurrentAccount(@RequestBody AccountRequest accountRequest);

	@PostMapping("/fixed-term")
	ApiResponse<AccountResponse> createFixedTermAccount(@RequestBody AccountRequest accountRequest);

	@GetMapping("/balance/{accountId}")
	ApiResponse<BalanceResponse> checkBalance(@PathVariable Integer accountId);

}
