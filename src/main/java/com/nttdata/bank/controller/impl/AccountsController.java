package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.AccountsAPI;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.service.AccountsService;
import java.util.List;

/**
 * * AccountsController is a REST controller that implements the AccountsAPI
 * interface. * This class handles HTTP requests related to account operations
 * such as creating, * retrieving, updating, and deleting accounts, as well as
 * checking account balance. * It delegates the actual business logic to the
 * AccountService.
 */

@RestController
public class AccountsController implements AccountsAPI {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	AccountsService accountService;

	/**
	 * Creates a new account.
	 *
	 * @param accountRequest The account request payload
	 * @return ApiResponse containing the account response
	 */
	@Override
	public ApiResponse<AccountResponse> createAccount(AccountRequest accountRequest) {
		logger.debug("Received request to create account: {}", accountRequest);
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.registerAccount(accountRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Account created successfully");
		response.setData(accountResponse);
		logger.info("Account created successfully: {}", accountResponse);
		return response;
	}

	/**
	 * Checks the balance of an account.
	 *
	 * @param accountNumber The account number
	 * @return ApiResponse containing the balance response
	 */
	@Override
	public ApiResponse<BalanceResponse> checkBalance(String accountNumber) {
		logger.debug("Received request to check balance for account: {}", accountNumber);
		ApiResponse<BalanceResponse> response = new ApiResponse<>();
		BalanceResponse balanceResponse = accountService.checkBalance(accountNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Balance retrieved successfully");
		response.setData(balanceResponse);
		logger.info("Balance retrieved successfully for account: {}", accountNumber);
		return response;
	}

	/**
	 * Finds all accounts.
	 *
	 * @return ApiResponse containing the list of account responses
	 */
	@Override
	public ApiResponse<List<AccountResponse>> findAllAccounts() {
		logger.debug("Received request to find all accounts");
		ApiResponse<List<AccountResponse>> response = new ApiResponse<>();
		List<AccountResponse> accounts = accountService.findAllAccounts();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Accounts retrieved successfully");
		response.setData(accounts);
		logger.info("All accounts retrieved successfully");
		return response;
	}

	/**
	 * Updates an account. 
	 *
	 * @param accountNumber The account number
	 * @return ApiResponse containing the updated account response
	 */
	@Override
	public ApiResponse<AccountResponse> updateAccountAllowWithdrawals(String accountNumber) {
		logger.debug("Received request to update account with accountNumber: {}", accountNumber);
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.updateAccountAllowWithdrawals(accountNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account updated successfully");
		response.setData(accountResponse);
		logger.info("Account updated successfully with accountNumber: {}", accountNumber);
		return response;
	}

	/**
	 * Deletes an account.
	 *
	 * @param accountNumber The account number
	 * @return ApiResponse indicating the result of the delete operation
	 */

	public ApiResponse<Void> deleteAccount(String accountNumber) {
		logger.debug("Received request to delete account with accountNumber: {}", accountNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		accountService.deleteAccount(accountNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Account deleted successfully");
		logger.info("Account deleted successfully with accountNumber: {}", accountNumber);
		return response;
	}
}
