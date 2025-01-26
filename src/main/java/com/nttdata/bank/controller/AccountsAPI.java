package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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

/**
 * AccountsAPI defines the RESTful endpoints for account-related operations.
 * This interface includes methods for creating, retrieving, updating, and
 * deleting accounts, as well as checking account balance. Each method maps to
 * an HTTP request and returns a structured API response.
 */
@RestController
@RequestMapping("/account")
public interface AccountsAPI {

	/**
	 * Creates a new account based on the provided AccountRequest object.
	 * 
	 * @param accountRequest - The account details provided in the request body.
	 * @return ApiResponse containing the created AccountResponse.
	 */
	@PostMapping("/create")
	ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest);

	/**
	 * Checks the balance of the specified account.
	 * 
	 * @param accountNumber - The account number for which the balance is to be
	 *                      checked.
	 * @return ApiResponse containing the BalanceResponse.
	 */
	@GetMapping("/balance/{accountNumber}")
	ApiResponse<BalanceResponse> checkBalance(@PathVariable String accountNumber);

	/**
	 * Retrieves a list of all accounts by customer.
	 * 
	 * @param documentNumber - The document number for which the accounts are to be
	 *                       retrieved.
	 * @return ApiResponse containing a list of AccountResponse objects.
	 */
	@GetMapping("/all/{documentNumber}")
	ApiResponse<List<AccountResponse>> findAllAccounts(@PathVariable String documentNumber);

	/**
	 * Deletes the specified account based on the account number.
	 * 
	 * @param accountNumber - The account number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@DeleteMapping("/delete/{accountNumber}")
	ApiResponse<Void> deleteAccount(@PathVariable String accountNumber);
}
