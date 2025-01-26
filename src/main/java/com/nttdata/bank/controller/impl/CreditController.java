package com.nttdata.bank.controller.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditAPI;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.service.CreditService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * CreditController is a REST controller that implements the CreditAPI
 * interface. This class handles HTTP requests related to credit operations such
 * as granting credits, checking credit debt, and retrieving all credits. It
 * delegates the actual business logic to the CreditService.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class CreditController implements CreditAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

	@Autowired
	CreditService creditService;

	/**
	 * Grants a new credit based on the provided CreditRequest object. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param creditRequest - The credit details provided in the request body.
	 * @return ApiResponse containing the granted CreditResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditService", fallbackMethod = "fallbackGrantCredit")
	@TimeLimiter(name = "creditService")
	public ApiResponse<CreditResponse> grantCredit(CreditRequest creditRequest) {
		logger.debug("Received request to grant credit: {}", creditRequest);
		ApiResponse<CreditResponse> response = new ApiResponse<>();
		CreditResponse creditResponse = creditService.grantCredit(creditRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit granted successfully.");
		response.setData(creditResponse);
		logger.info("Credit granted successfully: {}", creditResponse);
		return response;
	}

	/**
	 * Checks the debt for the specified credit. Utilizes CircuitBreaker and
	 * TimeLimiter to handle resilience.
	 *
	 * @param creditId - The ID of the credit for which the debt is to be checked.
	 * @return ApiResponse containing the CreditDebtResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditService", fallbackMethod = "fallbackCheckDebtCredit")
	@TimeLimiter(name = "creditService")
	public ApiResponse<CreditDebtResponse> checkDebtCredit(String creditId) {
		logger.debug("Received request to check debt for credit: {}", creditId);
		ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
		CreditDebtResponse creditDebtResponse = creditService.checkDebtCredit(creditId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Debt retrieved successfully.");
		response.setData(creditDebtResponse);
		logger.info("Debt retrieved successfully for credit: {}", creditId);
		return response;
	}

	/**
	 * Retrieves a list of all credits. Utilizes CircuitBreaker and TimeLimiter to
	 * handle resilience.
	 *
	 * @return ApiResponse containing a list of CreditResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "creditService", fallbackMethod = "fallbackFindAllCredits")
	@TimeLimiter(name = "creditService")
	public ApiResponse<List<CreditResponse>> findAllCredits() {
		logger.debug("Received request to find all credits.");
		ApiResponse<List<CreditResponse>> response = new ApiResponse<>();
		List<CreditResponse> creditResponses = creditService.findAllCredits();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credits retrieved successfully.");
		response.setData(creditResponses);
		logger.info("All credits retrieved successfully.");
		return response;
	}

	/**
	 * Fallback method for grantCredit in case of failure or timeout.
	 *
	 * @param creditRequest - The original credit request.
	 * @param throwable     - The exception that caused the fallback to be
	 *                      triggered.
	 * @return ApiResponse indicating failure to grant credit.
	 */
	public ApiResponse<CreditResponse> fallbackGrantCredit(CreditRequest creditRequest, Throwable throwable) {
		logger.error("Fallback method for grantCredit due to: {}", throwable.getMessage());
		ApiResponse<CreditResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to grant credit at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for checkDebtCredit in case of failure or timeout.
	 *
	 * @param creditId  - The credit ID being checked.
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to check credit debt.
	 */
	public ApiResponse<CreditDebtResponse> fallbackCheckDebtCredit(String creditId, Throwable throwable) {
		logger.error("Fallback method for checkDebtCredit due to: {}", throwable.getMessage());
		ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to check credit debt at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllCredits in case of failure or timeout.
	 *
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to retrieve credits.
	 */
	public ApiResponse<List<CreditResponse>> fallbackFindAllCredits(Throwable throwable) {
		logger.error("Fallback method for findAllCredits due to: {}", throwable.getMessage());
		ApiResponse<List<CreditResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve credits at the moment. Please try again later.");
		return response;
	}
}
