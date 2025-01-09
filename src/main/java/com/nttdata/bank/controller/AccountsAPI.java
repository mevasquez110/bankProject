package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import java.util.List;

@RestController
@RequestMapping("/account")
public interface AccountsAPI {

	@PostMapping("/create")
	ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest);

	@GetMapping("/balance")
	ApiResponse<BalanceResponse> checkBalance(@RequestParam String accountNumber);

	@GetMapping("/all")
	ApiResponse<List<AccountResponse>> findAllAccounts();

	@PutMapping("/update/{accountId}")
	ApiResponse<AccountResponse> updateAccount(@PathVariable String accountId);

	@DeleteMapping("/delete/{accountId}")
	ApiResponse<Void> deleteAccount(@PathVariable String accountId);
}
