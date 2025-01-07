package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;

@RestController
@RequestMapping("/account")
public interface AccountsAPI {

	@PostMapping("/create")
	ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest);

	@GetMapping("/balance")
	ApiResponse<BalanceResponse> checkBalance(@RequestParam String accountNumber);

}
