package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;

/**
 * AccountsService provides the service layer for handling account-related
 * operations. This interface defines methods for registering an account,
 * checking account balance, finding all accounts, updating an account, and
 * deleting an account.
 */

public interface AccountsService {

	/**
	 * Registers a new account.
	 *
	 * @param accountRequest The account request to register a new account
	 * @return The response containing account details
	 */
	AccountResponse registerAccount(AccountRequest accountRequest);

	/**
	 * Checks the balance of an account.
	 *
	 * @param accountNumber The account number to check the balance for
	 * @return The response containing balance details
	 */
	BalanceResponse checkBalance(String accountNumber);

	/**
	 * Finds all accounts.
	 * 
	 * @param documentNumber The document number to find accounts for
	 * @return A list of responses containing account details
	 */
	List<AccountResponse> findAllAccounts(String documentNumber);

	/**
	 * Updates an existing account.
	 *
	 * @param updateAccountRequest The request containing updated account details
	 * @return The response containing updated account details
	 */
	AccountResponse updateAccount(UpdateAccountRequest updateAccountRequest);

	/**
	 * Deletes an account.
	 *
	 * @param accountNumber The account number to delete
	 */
	void deleteAccount(String accountNumber);
}
