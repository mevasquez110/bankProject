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

/**
 * CreditController is a REST controller that implements the CreditAPI
 * interface. This class handles HTTP requests related to credit operations such
 * as granting credits, checking credit debt, and retrieving all credits. It
 * delegates the actual business logic to the CreditService.
 */
@RestController
public class CreditController implements CreditAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

	@Autowired
	CreditService creditService;

	/**
	 * Grants a new credit based on the provided CreditRequest object.
	 *
	 * @param creditRequest - The credit details provided in the request body.
	 * @return ApiResponse containing the granted CreditResponse.
	 */
	@Override
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
	 * Checks the debt for the specified credit.
	 *
	 * @param creditId - The ID of the credit for which the debt is to be checked.
	 * @return ApiResponse containing the CreditDebtResponse.
	 */
	@Override
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
	 * Retrieves a list of all credits.
	 *
	 * @return ApiResponse containing a list of CreditResponse objects.
	 */
	@Override
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
}
