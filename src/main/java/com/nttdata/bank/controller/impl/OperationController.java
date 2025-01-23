package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.OperationAPI;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.ProductResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.OperationService;

/**
 * OperationController is a REST controller that implements the OperationAPI
 * interface. This class handles HTTP requests related to transaction operations
 * such as making deposits, withdrawals, paying credit installments, checking
 * transaction history, and mobile transfers. It delegates the actual business
 * logic to the appropriate service layer.
 */
@RestController
public class OperationController implements OperationAPI {

	private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

	@Autowired
	OperationService transactionService;

	/**
	 * Makes a deposit.
	 *
	 * @param depositRequest The deposit request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> makeDeposit(@Valid DepositRequest depositRequest) {
		logger.debug("Received request to make a deposit: {}", depositRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeDeposit(depositRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Deposit made successfully");
		response.setData(transactionResponse);
		logger.info("Deposit made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Makes a withdrawal.
	 *
	 * @param withdrawalRequest The withdrawal request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> makeWithdrawal(@Valid WithdrawalRequest withdrawalRequest) {
		logger.debug("Received request to make a withdrawal: {}", withdrawalRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeWithdrawal(withdrawalRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Withdrawal made successfully");
		response.setData(transactionResponse);
		logger.info("Withdrawal made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Makes an account transfer.
	 *
	 * @param accountTransferRequest The account transfer request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> makeAccountTransfer(@Valid AccountTransferRequest accountTransferRequest) {
		logger.debug("Received request to make an account transfer: {}", accountTransferRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeAccountTransfer(accountTransferRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account transfer made successfully");
		response.setData(transactionResponse);
		logger.info("Account transfer made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Makes a mobile transfer.
	 *
	 * @param mobileTransferRequest The mobile transfer request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> makeMobileTransfer(@Valid MobileTransferRequest mobileTransferRequest) {
		logger.debug("Received request to make a mobile transfer: {}", mobileTransferRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeMobileTransfer(mobileTransferRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Mobile transfer made successfully");
		response.setData(transactionResponse);
		logger.info("Mobile transfer made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Pays a credit card.
	 *
	 * @param payCreditCardRequest The credit card payment request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> payCreditCard(@Valid PayCreditCardRequest payCreditCardRequest) {
		logger.debug("Received request to pay credit card: {}", payCreditCardRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.payCreditCard(payCreditCardRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit card paid successfully");
		response.setData(transactionResponse);
		logger.info("Credit card paid successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Pays credit installments.
	 *
	 * @param payCreditRequest The credit payment request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	public ApiResponse<TransactionResponse> payCredit(@Valid PayCreditRequest payCreditRequest) {
		logger.debug("Received request to pay credit: {}", payCreditRequest);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.payCredit(payCreditRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit installment paid successfully");
		response.setData(transactionResponse);
		logger.info("Credit installment paid successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Checks transactions.
	 *
	 * @param documentNumber The document number
	 * @return ApiResponse containing the list of transaction responses
	 */
	@Override
	public ApiResponse<List<TransactionResponse>> checkTransactions(String documentNumber) {
		logger.debug("Received request to check transactions for document number: {}", documentNumber);
		ApiResponse<List<TransactionResponse>> response = new ApiResponse<>();
		List<TransactionResponse> transactions = transactionService.checkTransactions(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Transactions retrieved successfully");
		response.setData(transactions);
		logger.info("Transactions retrieved successfully for document number: {}", documentNumber);
		return response;
	}

	/**
	 * Gets the products.
	 *
	 * @param documentNumber The document number
	 * @return ApiResponse containing the list of product responses
	 */
	@Override
	public ApiResponse<List<ProductResponse>> getProducts(String documentNumber) {
		logger.debug("Received request to get products for document number: {}", documentNumber);
		ApiResponse<List<ProductResponse>> response = new ApiResponse<>();
		List<ProductResponse> products = transactionService.getProducts(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Products retrieved successfully");
		response.setData(products);
		logger.info("Products retrieved successfully for document number: {}", documentNumber);
		return response;
	}
}
