package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.ProductResponse;
import com.nttdata.bank.response.TransactionResponse;

/**
 * OperationAPI defines the RESTful endpoints for various banking operations.
 * This interface includes methods for making deposits, withdrawals, account
 * transfers, mobile transfers, credit card payments, and credit payments.
 * Additionally, it provides methods for checking transactions and retrieving
 * account-linked products.
 */
@RestController
@RequestMapping("/operation")
public interface OperationAPI {

	/**
	 * Makes a deposit based on the provided DepositRequest object.
	 *
	 * @param depositRequest - The deposit details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse of the deposit.
	 */
	@PostMapping("/deposit")
	ApiResponse<TransactionResponse> makeDeposit(@RequestBody @Valid DepositRequest depositRequest);

	/**
	 * Makes a withdrawal based on the provided WithdrawalRequest object.
	 *
	 * @param withdrawalRequest - The withdrawal details provided in the request
	 *                          body.
	 * @return ApiResponse containing the TransactionResponse of the withdrawal.
	 */
	@PostMapping("/withdraw")
	ApiResponse<TransactionResponse> makeWithdrawal(@RequestBody @Valid WithdrawalRequest withdrawalRequest);

	/**
	 * Makes an account transfer based on the provided AccountTransferRequest
	 * object.
	 *
	 * @param accountTransferRequest - The account transfer details provided in the
	 *                               request body.
	 * @return ApiResponse containing the TransactionResponse of the transfer.
	 */
	@PostMapping("/account-transfer")
	ApiResponse<TransactionResponse> makeAccountTransfer(
			@RequestBody @Valid AccountTransferRequest accountTransferRequest);

	/**
	 * Makes a mobile transfer based on the provided MobileTransferRequest object.
	 *
	 * @param mobileTransferRequest - The mobile transfer details provided in the
	 *                              request body.
	 * @return ApiResponse containing the TransactionResponse of the transfer.
	 */
	@PostMapping("/mobile-transfer")
	ApiResponse<TransactionResponse> makeMobileTransfer(
			@RequestBody @Valid MobileTransferRequest mobileTransferRequest);

	/**
	 * Pays a credit card bill based on the provided PayCreditCardRequest object.
	 *
	 * @param payCreditCardRequest - The payment details provided in the request
	 *                             body.
	 * @return ApiResponse containing the TransactionResponse of the payment.
	 */
	@PostMapping("/pay-credit-card")
	ApiResponse<TransactionResponse> payCreditCard(@RequestBody @Valid PayCreditCardRequest payCreditCardRequest);

	/**
	 * Pays a credit bill based on the provided PayCreditRequest object.
	 *
	 * @param payCreditRequest - The payment details provided in the request body.
	 * @return ApiResponse containing the TransactionResponse of the payment.
	 */
	@PostMapping("/pay-credit")
	ApiResponse<TransactionResponse> payCredit(@RequestBody @Valid PayCreditRequest payCreditRequest);

	/**
	 * Checks all transactions related to the specified document number.
	 *
	 * @param documentNumber - The document number of the customer to check
	 *                       transactions for.
	 * @return ApiResponse containing a list of TransactionResponse objects.
	 */
	@GetMapping("check/{documentNumber}")
	ApiResponse<List<TransactionResponse>> checkTransactions(@PathVariable String documentNumber);

	/**
	 * Retrieves products linked to the specified document number.
	 *
	 * @param documentNumber - The document number of the customer to retrieve
	 *                       products for.
	 * @return ApiResponse containing a list of ProductResponse objects.
	 */
	@GetMapping("products/{documentNumber}")
	ApiResponse<List<ProductResponse>> getProducts(@PathVariable String documentNumber);
}
