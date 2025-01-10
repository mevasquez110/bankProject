package com.nttdata.bank.controller.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.TransactionAPI;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * TransactionController is a REST controller that implements the TransactionAPI
 * interface. This class handles HTTP requests related to transaction operations
 * such as making deposits, withdrawals, paying installments, checking
 * transaction history, and charging consumptions. It delegates the actual
 * business logic to the appropriate service layer.
 */
@RestController
public class TransactionController implements TransactionAPI {

	private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	TransactionService transactionService;

	/**
	 * Makes a deposit transaction. This method is transactional and uses a circuit
	 * breaker for resilience.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackMakeDeposit")
	public ApiResponse<TransactionResponse> makeDeposit(TransactionRequest transaction) {
		logger.debug("Received request to make deposit: {}", transaction);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeDeposit(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Deposit made successfully");
		response.setData(transactionResponse);
		logger.info("Deposit made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Fallback method for makeDeposit in case of failure.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<TransactionResponse> fallbackMakeDeposit(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	/**
	 * Makes a withdrawal transaction. This method is transactional and uses a
	 * circuit breaker for resilience.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackMakeWithdrawal")
	public ApiResponse<TransactionResponse> makeWithdrawal(TransactionRequest transaction) {
		logger.debug("Received request to make withdrawal: {}", transaction);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeWithdrawal(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Withdrawal made successfully");
		response.setData(transactionResponse);
		logger.info("Withdrawal made successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Fallback method for makeWithdrawal in case of failure.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<TransactionResponse> fallbackMakeWithdrawal(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	/**
	 * Pays an installment for a credit. This method is transactional and uses a
	 * circuit breaker for resilience.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackPayInstallment")
	public ApiResponse<TransactionResponse> payInstallment(TransactionRequest transaction) {
		logger.debug("Received request to pay installment: {}", transaction);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.payInstallment(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Installment paid successfully");
		response.setData(transactionResponse);
		logger.info("Installment paid successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Fallback method for payInstallment in case of failure.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<TransactionResponse> fallbackPayInstallment(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}

	/**
	 * Checks all transactions of an account. This method is transactional and uses
	 * a circuit breaker for resilience.
	 *
	 * @param accountId The account ID
	 * @return ApiResponse containing the list of transaction responses
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "transactionService")
	public ApiResponse<List<TransactionResponse>> checkTransactions(Integer accountId) {
		logger.debug("Received request to check transactions for account ID: {}", accountId);
		ApiResponse<List<TransactionResponse>> response = new ApiResponse<>();
		List<TransactionResponse> transactionResponses = transactionService.checkTransactions(accountId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Transactions retrieved successfully");
		response.setData(transactionResponses);
		logger.info("Transactions retrieved successfully for account ID: {}", accountId);
		return response;
	}

	/**
	 * Charges consumption on a credit card. This method is transactional and uses a
	 * circuit breaker for resilience.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@Transactional
	@CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackChargeConsumption")
	public ApiResponse<TransactionResponse> chargeConsumption(TransactionRequest transaction) {
		logger.debug("Received request to charge consumption: {}", transaction);
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.chargeConsumption(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Consumption charged successfully");
		response.setData(transactionResponse);
		logger.info("Consumption charged successfully: {}", transactionResponse);
		return response;
	}

	/**
	 * Fallback method for chargeConsumption in case of failure.
	 *
	 * @param transaction The transaction request payload
	 * @return ApiResponse containing the fallback response
	 */
	public ApiResponse<TransactionResponse> fallbackChargeConsumption(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		response.setMessage("Service is currently unavailable. Please try again later.");
		return response;
	}
}
