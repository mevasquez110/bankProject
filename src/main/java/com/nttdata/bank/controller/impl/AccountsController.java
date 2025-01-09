package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	AccountsService accountService;

	/**
	 * Creates a new account. This method is transactional and uses a circuit
	 * breaker for resilience.
	 *
	 * @param accountRequest The account request payload
	 * @return ApiResponse containing the account response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "accountsService", fallbackMethod = "fallbackCreateAccount")
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
	 * Fallback method for createAccount in case of failure.
	 *
	 * @param accountRequest The account request payload
	 * @param t              The throwable cause of the failure
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<AccountResponse> fallbackCreateAccount(AccountRequest accountRequest, Throwable t) {
		logger.error("Fallback method triggered for createAccount due to: {}", t.getMessage());
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	/**
	 * Checks the balance of an account. This method is transactional and uses a
	 * circuit breaker for resilience.
	 *
	 * @param accountNumber The account number
	 * @return ApiResponse containing the balance response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "accountsService")
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
	 * Finds all accounts. This method is transactional and uses a circuit breaker
	 * for resilience.
	 *
	 * @return ApiResponse containing the list of account responses
	 */
	@Transactional
	@CircuitBreaker(name = "accountsService")
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
	 * Updates an account. This method is transactional and uses a circuit breaker
	 * for resilience.
	 *
	 * @param accountId The account ID
	 * @return ApiResponse containing the updated account response
	 */
	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<AccountResponse> updateAccount(String accountId) {
		logger.debug("Received request to update account with ID: {}", accountId);
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.updateAccount(accountId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account updated successfully");
		response.setData(accountResponse);
		logger.info("Account updated successfully with ID: {}", accountId);
		return response;
	}

	/**
	 * Deletes an account. This method is transactional and uses a circuit breaker
	 * for resilience.
	 *
	 * @param accountId The account ID
	 * @return ApiResponse indicating the result of the delete operation
	 */
	@Transactional
	@CircuitBreaker(name = "accountsService")
	public ApiResponse<Void> deleteAccount(String accountId) {
		logger.debug("Received request to delete account with ID: {}", accountId);
		ApiResponse<Void> response = new ApiResponse<>();
		accountService.deleteAccount(accountId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Account deleted successfully");
		logger.info("Account deleted successfully with ID: {}", accountId);
		return response;
	}
}
