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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import reactor.core.publisher.Mono;

/**
 * OperationController is a REST controller that implements the OperationAPI
 * interface. This class handles HTTP requests related to transaction operations
 * such as making deposits, withdrawals, paying credit installments, checking
 * transaction history, and mobile transfers. It delegates the actual business
 * logic to the appropriate service layer.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class OperationController implements OperationAPI {

	private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

	@Autowired
	OperationService transactionService;

	/**
	 * Makes a deposit. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param depositRequest The deposit request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackMakeDeposit")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> makeDeposit(
			@Valid Mono<DepositRequest> depositRequest) {
		logger.debug("Received request to make a deposit: {}", depositRequest);
		return depositRequest
				.flatMap(request -> transactionService.makeDeposit(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Deposit made successfully");
					response.setData(transactionResponse);
					logger.info("Deposit made successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Makes a withdrawal. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param withdrawalRequest The withdrawal request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackMakeWithdrawal")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> makeWithdrawal(
			@Valid Mono<WithdrawalRequest> withdrawalRequest) {
		logger.debug("Received request to make a withdrawal: {}", withdrawalRequest);
		return withdrawalRequest
				.flatMap(request -> transactionService.makeWithdrawal(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Withdrawal made successfully");
					response.setData(transactionResponse);
					logger.info("Withdrawal made successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Makes an account transfer. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param accountTransferRequest The account transfer request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackMakeAccountTransfer")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> makeAccountTransfer(
			@Valid Mono<AccountTransferRequest> accountTransferRequest) {
		logger.debug("Received request to make an account transfer: {}", accountTransferRequest);
		return accountTransferRequest
				.flatMap(request -> transactionService.makeAccountTransfer(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Account transfer made successfully");
					response.setData(transactionResponse);
					logger.info("Account transfer made successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Makes a mobile transfer. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param mobileTransferRequest The mobile transfer request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackMakeMobileTransfer")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> makeMobileTransfer(
			@Valid Mono<MobileTransferRequest> mobileTransferRequest) {
		logger.debug("Received request to make a mobile transfer: {}", mobileTransferRequest);
		return mobileTransferRequest
				.flatMap(request -> transactionService.makeMobileTransfer(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Mobile transfer made successfully");
					response.setData(transactionResponse);
					logger.info("Mobile transfer made successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Pays a credit card. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param payCreditCardRequest The credit card payment request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackPayCreditCard")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> payCreditCard(
			@Valid Mono<PayCreditCardRequest> payCreditCardRequest) {
		logger.debug("Received request to pay credit card: {}", payCreditCardRequest);
		return payCreditCardRequest
				.flatMap(request -> transactionService.payCreditCard(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Credit card paid successfully");
					response.setData(transactionResponse);
					logger.info("Credit card paid successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Pays credit installments. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param payCreditRequest The credit payment request payload
	 * @return ApiResponse containing the transaction response
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackPayCredit")
	@TimeLimiter(name = "operationService")
	public Mono<ApiResponse<TransactionResponse>> payCredit(
			@Valid Mono<PayCreditRequest> payCreditRequest) {
		logger.debug("Received request to pay credit: {}", payCreditRequest);
		return payCreditRequest
				.flatMap(request -> transactionService.payCredit(request))
				.map(transactionResponse -> {
					ApiResponse<TransactionResponse> response = new ApiResponse<>();
					response.setStatusCode(HttpStatus.OK.value());
					response.setMessage("Credit installment paid successfully");
					response.setData(transactionResponse);
					logger.info("Credit installment paid successfully: {}", transactionResponse);
					return response;
				});
	}

	/**
	 * Checks transactions. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param documentNumber The document number
	 * @return ApiResponse containing the list of transaction responses
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackCheckTransactions")
	@TimeLimiter(name = "operationService")
	public ApiResponse<List<TransactionResponse>> checkTransactions(String documentNumber) {
		logger.debug("Received request to check transactions for document number: {}",
				documentNumber);
		ApiResponse<List<TransactionResponse>> response = new ApiResponse<>();
		List<TransactionResponse> transactions = transactionService
				.checkTransactions(documentNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Transactions retrieved successfully");
		response.setData(transactions);
		logger.info("Transactions retrieved successfully for document number: {}", documentNumber);
		return response;
	}

	/**
	 * Gets the products. Utilizes CircuitBreaker and TimeLimiter to handle
	 * resilience.
	 *
	 * @param documentNumber The document number
	 * @return ApiResponse containing the list of product responses
	 */
	@Override
	@CircuitBreaker(name = "operationService", fallbackMethod = "fallbackGetProducts")
	@TimeLimiter(name = "operationService")
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

	/**
	 * Fallback method for makeDeposit in case of failure or timeout.
	 *
	 * @param depositRequest - The original deposit request.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to make deposit.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackMakeDeposit(
			Mono<DepositRequest> depositRequest, Throwable throwable) {
		logger.error("Fallback method for makeDeposit due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to make deposit at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for makeWithdrawal in case of failure or timeout.
	 *
	 * @param withdrawalRequest - The original withdrawal request.
	 * @param throwable         - The exception that caused the fallback to be
	 *                          triggered.
	 * @return ApiResponse indicating failure to make withdrawal.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackMakeWithdrawal(
			Mono<WithdrawalRequest> withdrawalRequest, Throwable throwable) {
		logger.error("Fallback method for makeWithdrawal due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to make withdrawal at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for makeAccountTransfer in case of failure or timeout.
	 *
	 * @param accountTransferRequest - The original account transfer request.
	 * @param throwable              - The exception that caused the fallback to be
	 *                               triggered.
	 * @return ApiResponse indicating failure to make account transfer.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackMakeAccountTransfer(
			Mono<AccountTransferRequest> accountTransferRequest, Throwable throwable) {
		logger.error("Fallback method for makeAccountTransfer due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage(
				"Unable to make account transfer at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for makeMobileTransfer in case of failure or timeout.
	 *
	 * @param mobileTransferRequest - The original mobile transfer request.
	 * @param throwable             - The exception that caused the fallback to be
	 *                              triggered.
	 * @return ApiResponse indicating failure to make mobile transfer.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackMakeMobileTransfer(
			Mono<MobileTransferRequest> mobileTransferRequest, Throwable throwable) {
		logger.error("Fallback method for makeMobileTransfer due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage(
				"Unable to make mobile transfer at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for payCreditCard in case of failure or timeout.
	 *
	 * @param payCreditCardRequest - The original credit card payment request.
	 * @param throwable            - The exception that caused the fallback to be
	 *                             triggered.
	 * @return ApiResponse indicating failure to pay credit card.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackPayCreditCard(
			Mono<PayCreditCardRequest> payCreditCardRequest, Throwable throwable) {
		logger.error("Fallback method for payCreditCard due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to pay credit card at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for payCredit in case of failure or timeout.
	 *
	 * @param payCreditRequest - The original credit payment request.
	 * @param throwable        - The exception that caused the fallback to be
	 *                         triggered.
	 * @return ApiResponse indicating failure to pay credit.
	 */
	public Mono<ApiResponse<TransactionResponse>> fallbackPayCredit(
			Mono<PayCreditRequest> payCreditRequest, Throwable throwable) {
		logger.error("Fallback method for payCredit due to: {}", throwable.getMessage());
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to pay credit at the moment. Please try again later.");
		return Mono.just(response);
	}

	/**
	 * Fallback method for checkTransactions in case of failure or timeout.
	 *
	 * @param documentNumber - The document number for the transactions being
	 *                       checked.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to check transactions.
	 */
	public ApiResponse<List<TransactionResponse>> fallbackCheckTransactions(
			String documentNumber,
			Throwable throwable) {
		logger.error("Fallback method for checkTransactions due to: {}", throwable.getMessage());
		ApiResponse<List<TransactionResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to check transactions at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for getProducts in case of failure or timeout.
	 *
	 * @param documentNumber - The document number for the products being retrieved.
	 * @param throwable      - The exception that caused the fallback to be
	 *                       triggered.
	 * @return ApiResponse indicating failure to get products.
	 */
	public ApiResponse<List<ProductResponse>> fallbackGetProducts(String documentNumber,
			Throwable throwable) {
		logger.error("Fallback method for getProducts due to: {}", throwable.getMessage());
		ApiResponse<List<ProductResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve products at the moment. Please try again later.");
		return response;
	}
}
