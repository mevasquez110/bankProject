package com.nttdata.bank.controller.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.AccountsAPI;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.service.AccountsService;

/**
 * AccountsController is a REST controller that implements the AccountsAPI
 * interface. This class handles HTTP requests related to account operations
 * such as creating, retrieving, updating, and deleting accounts, as well as
 * checking account balances. It delegates the actual business logic to the
 * AccountsService.
 */
@RestController
public class AccountsController implements AccountsAPI {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	AccountsService accountService;

	/**
	 * Creates a new account based on the provided AccountRequest object.
	 *
	 * @param accountRequest - The account details provided in the request body.
	 * @return ApiResponse containing the created AccountResponse.
	 */
	@Override
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
	 * Checks the balance for the specified account.
	 *
	 * @param accountNumber - The account number for which the balance is to be
	 *                      checked.
	 * @return ApiResponse containing the BalanceResponse.
	 */
	@Override
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
	 * number.
	 *
	 * @param documentNumber - The document number for which the accounts are to be
	 *                       retrieved.
	 * @return ApiResponse containing a list of AccountResponse objects.
	 */
	@Override
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
	 * Updates the specified account based on the provided UpdateAccountRequest
	 * object.
	 *
	 * @param updateAccountRequest - The updated account details provided in the
	 *                             request body.
	 * @return ApiResponse containing the updated AccountResponse.
	 */
	@Override
	public ApiResponse<AccountResponse> updateAccount(UpdateAccountRequest updateAccountRequest) {
		logger.debug("Received request to update account: {}", updateAccountRequest);
		ApiResponse<AccountResponse> response = new ApiResponse<>();
		AccountResponse accountResponse = accountService.updateAccount(updateAccountRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account updated successfully.");
		response.setData(accountResponse);
		logger.info("Account updated successfully: {}", accountResponse);
		return response;
	}

	/**
	 * Deletes the specified account based on the account number.
	 *
	 * @param accountNumber - The account number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	public ApiResponse<Void> deleteAccount(String accountNumber) {
		logger.debug("Received request to delete account with account number: {}", accountNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		accountService.deleteAccount(accountNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Account deleted successfully.");
		logger.info("Account deleted successfully with account number: {}", accountNumber);
		return response;
	}
}
