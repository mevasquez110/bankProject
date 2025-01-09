package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;

/**
 * TransactionAPI defines the RESTful endpoints for transaction-related
 * operations. This interface includes methods for making deposits, withdrawals,
 * paying installments, checking transaction history, and charging consumptions.
 * Each method maps to an HTTP request and returns a structured API response.
 */

@RestController
@RequestMapping("/transactions")
public interface TransactionAPI {

	/**
	 * Makes a deposit based on the provided TransactionRequest object.
	 * 
	 * @param transaction - The transaction details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse.
	 */
	@PostMapping("/deposit")
	ApiResponse<TransactionResponse> makeDeposit(@RequestBody @Valid TransactionRequest transaction);

	/**
	 * Makes a withdrawal based on the provided TransactionRequest object.
	 * 
	 * @param transaction - The transaction details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse.
	 */
	@PostMapping("/withdraw")
	ApiResponse<TransactionResponse> makeWithdrawal(@RequestBody @Valid TransactionRequest transaction);

	/**
	 * Pays an installment based on the provided TransactionRequest object.
	 * 
	 * @param transaction - The transaction details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse.
	 */
	@PostMapping("/pay-installment")
	ApiResponse<TransactionResponse> payInstallment(@RequestBody @Valid TransactionRequest transaction);

	/**
	 * Checks the transactions for a specific account.
	 * 
	 * @param accountId - The ID of the account for which the transactions are to be
	 *                  checked.
	 * @return ApiResponse containing a list of TransactionResponse objects.
	 */
	@GetMapping("/{accountId}")
	ApiResponse<List<TransactionResponse>> checkTransactions(@PathVariable Integer accountId);

	/**
	 * Charges a consumption based on the provided TransactionRequest object.
	 * 
	 * @param transaction - The transaction details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse.
	 */
	@PostMapping("/charge")
	ApiResponse<TransactionResponse> chargeConsumption(@RequestBody @Valid TransactionRequest transaction);
}
