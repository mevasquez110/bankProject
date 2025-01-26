package com.nttdata.bank.controller.impl;

import java.util.List;
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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * AccountsController is a REST controller that implements the AccountsAPI
 * interface. This class handles HTTP requests related to account operations
 * such as creating, retrieving, updating, and deleting accounts, as well as
 * checking account balances. It delegates the actual business logic to the
 * AccountsService.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class AccountsController implements AccountsAPI {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	AccountsService accountService;

	/**
	 * Creates a new account based on the provided AccountRequest object. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param accountRequest - The account details provided in the request body.
	 * @return ApiResponse containing the created AccountResponse.
	 */
	@Override
	@CircuitBreaker(name = "accountService", fallbackMethod = "fallbackCreateAccount")
	@TimeLimiter(name = "accountService")
	public ApiResponse<AccountResponse> createAccount(AccountRequest accountRequest) {
		logger.debug("Received request to create account: {}", accountRequest);
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.registerAccount(accountRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Account successfully created.");
		response.setData(accountResponse);
		logger.info("Account created successfully: {}", accountResponse);
		return response;
	}

	/**
	 * Checks the balance for the specified account. Utilizes CircuitBreaker and
	 * TimeLimiter to handle resilience.
	 *
	 * @param accountNumber - The account number for which the balance is to be
	 *                      checked.
	 * @return ApiResponse containing the BalanceResponse.
	 */
	@Override
	@CircuitBreaker(name = "accountService", fallbackMethod = "fallbackCheckBalance")
	@TimeLimiter(name = "accountService")
	public ApiResponse<BalanceResponse> checkBalance(String accountNumber) {
		logger.debug("Received request to check balance for account: {}", accountNumber);
		ApiResponse<BalanceResponse> response = new ApiResponse<>();
		BalanceResponse balanceResponse = accountService.checkBalance(accountNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Balance retrieved successfully.");
		response.setData(balanceResponse);
		logger.info("Balance retrieved successfully for account: {}", accountNumber);
		return response;
	}

	/**
	 * Retrieves a list of all accounts associated with a customer's document
	 * number. Utilizes CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param documentNumber - The document number for which the accounts are to be
	 *                       retrieved.
	 * @return ApiResponse containing a list of AccountResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "accountService", fallbackMethod = "fallbackFindAllAccounts")
	@TimeLimiter(name = "accountService")
	public ApiResponse<List<AccountResponse>> findAllAccounts(String documentNumber) {
		logger.debug("Received request to find all accounts for document number: {}", documentNumber);
		ApiResponse<List<AccountResponse>> response = new ApiResponse<>();
		List<AccountResponse> accounts = accountService.findAllAccounts(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Accounts retrieved successfully.");
		response.setData(accounts);
		logger.info("All accounts retrieved successfully for document number: {}", documentNumber);
		return response;
	}

	/**
	 * Deletes the specified account based on the account number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param accountNumber - The account number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	@CircuitBreaker(name = "accountService", fallbackMethod = "fallbackDeleteAccount")
	@TimeLimiter(name = "accountService")
	public ApiResponse<Void> deleteAccount(String accountNumber) {
		logger.debug("Received request to delete account with account number: {}", accountNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		accountService.deleteAccount(accountNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Account deleted successfully.");
		logger.info("Account deleted successfully with account number: {}", accountNumber);
		return response;
	}

	/**
	 * Fallback method for createAccount in case of failure or timeout.
	 *
	 * @param accountRequest - The original account request.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to create account.
	 */
	public ApiResponse<AccountResponse> fallbackCreateAccount(AccountRequest accountRequest, Throwable throwable) {
		logger.error("Fallback method for createAccount due to: {}", throwable.getMessage());
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to create account at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for checkBalance in case of failure or timeout.
	 *
	 * @param accountNumber - The account number being checked.
	 * @param throwable     - The exception that caused the fallback to be
	 *                      triggered.
	 * @return ApiResponse indicating failure to check balance.
	 */
	public ApiResponse<BalanceResponse> fallbackCheckBalance(String accountNumber, Throwable throwable) {
		logger.error("Fallback method for checkBalance due to: {}", throwable.getMessage());
		ApiResponse<BalanceResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to check balance at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllAccounts in case of failure or timeout.
	 *
	 * @param documentNumber - The document number for which accounts were being
	 *                       retrieved.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to retrieve accounts.
	 */
	public ApiResponse<List<AccountResponse>> fallbackFindAllAccounts(String documentNumber, Throwable throwable) {
		logger.error("Fallback method for findAllAccounts due to: {}", throwable.getMessage());
		ApiResponse<List<AccountResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve accounts at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for deleteAccount in case of failure or timeout.
	 *
	 * @param accountNumber - The account number being deleted.
	 * @param throwable     - The exception that caused the fallback to be
	 *                      triggered.
	 * @return ApiResponse indicating failure to delete account.
	 */
	public ApiResponse<Void> fallbackDeleteAccount(String accountNumber, Throwable throwable) {
		logger.error("Fallback method for deleteAccount due to: {}", throwable.getMessage());
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to delete account at the moment. Please try again later.");
		return response;
	}
}
