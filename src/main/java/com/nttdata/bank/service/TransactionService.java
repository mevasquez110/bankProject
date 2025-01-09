package com.nttdata.bank.service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;

/**
 * TransactionService provides the service layer for handling
 * transaction-related operations. This interface defines methods for making
 * deposits, making withdrawals, paying installments, checking transactions, and
 * charging consumption.
 */

public interface TransactionService {

	/**
	 * Makes a deposit transaction.
	 *
	 * @param transaction The transaction request to process the deposit
	 * @return The response containing transaction details
	 */
	TransactionResponse makeDeposit(TransactionRequest transaction);

	/**
	 * Makes a withdrawal transaction.
	 *
	 * @param transaction The transaction request to process the withdrawal
	 * @return The response containing transaction details
	 */
	TransactionResponse makeWithdrawal(TransactionRequest transaction);

	/**
	 * Pays an installment transaction.
	 *
	 * @param transaction The transaction request to process the installment payment
	 * @return The response containing transaction details
	 */
	TransactionResponse payInstallment(TransactionRequest transaction);

	/**
	 * Checks transactions by account ID.
	 *
	 * @param accountId The account ID to check transactions for
	 * @return A list of responses containing transaction details
	 */
	List<TransactionResponse> checkTransactions(@PathVariable Integer accountId);

	/**
	 * Charges a consumption transaction.
	 *
	 * @param transaction The transaction request to process the consumption charge
	 * @return The response containing transaction details
	 */
	TransactionResponse chargeConsumption(TransactionRequest transaction);
}
