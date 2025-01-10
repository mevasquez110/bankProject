package com.nttdata.bank.client;

import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;

/**
 * TransactionClient defines the contract for transaction-related operations.
 * This interface includes methods for making deposit transactions.
 */
public interface TransactionClient {

	/**
	 * Makes a deposit transaction with the given transaction details.
	 *
	 * @param transactionRequest The details of the transaction to be made.
	 * @return The response containing the transaction details.
	 */
	TransactionResponse makeDeposit(TransactionRequest transactionRequest);
}
