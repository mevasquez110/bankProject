package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditAPI;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.service.CreditService;
import java.util.List;

/**
 * * CreditController is a REST controller that implements the CreditAPI
 * interface. * This class handles HTTP requests related to credit operations
 * such as granting credits, * checking credit debt, reprogramming debt,
 * retrieving all credits, and deleting credits. * It delegates the actual
 * business logic to the appropriate service layer.
 */

@RestController
public class CreditController implements CreditAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

	@Autowired
	CreditService creditService;

	/**
	 * Grants a new credit.
	 *
	 * @param creditRequest The credit request payload
	 * @return ApiResponse containing the credit response
	 */
	@Override
	public ApiResponse<CreditResponse> grantCredit(CreditRequest creditRequest) {
		logger.debug("Received request to grant credit: {}", creditRequest);
		ApiResponse<CreditResponse> response = new ApiResponse<>();
		CreditResponse creditResponse = creditService.grantCredit(creditRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit granted successfully");
		response.setData(creditResponse);
		logger.info("Credit granted successfully: {}", creditResponse);
		return response;
	}

	/**
	 * Checks the debt of a credit.
	 *
	 * @param creditId The credit ID
	 * @return ApiResponse containing the credit debt response
	 */
	@Override
	public ApiResponse<CreditDebtResponse> checkDebt(String creditId) {
		logger.debug("Received request to check debt for credit: {}", creditId);
		ApiResponse<CreditDebtResponse> response = new ApiResponse<>();
		CreditDebtResponse creditDebtResponse = creditService.checkDebt(creditId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Debt retrieved successfully");
		response.setData(creditDebtResponse);
		logger.info("Debt retrieved successfully for credit: {}", creditId);
		return response;
	}

	/**
	 * Finds all credits.
	 *
	 * @return ApiResponse containing the list of credit responses
	 */
	@Override
	public ApiResponse<List<CreditResponse>> findAllCredits() {
		logger.debug("Received request to find all credits");
		ApiResponse<List<CreditResponse>> response = new ApiResponse<>();
		List<CreditResponse> creditResponses = creditService.findAllCredits();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credits retrieved successfully");
		response.setData(creditResponses);
		logger.info("All credits retrieved successfully");
		return response;
	}

}
