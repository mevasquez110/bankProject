package com.nttdata.bank.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;
import java.util.List;

/**
 * CreditCardsController is a REST controller that implements the CreditCardsAPI
 * interface. This class handles HTTP requests related to credit card operations
 * such as requesting, checking debt, retrievinsg, updating, and deleting credit
 * cards. It delegates the actual business logic to the appropriate service
 * layer.
 */
@RestController
public class CreditCardsController implements CreditCardsAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardsController.class);

	@Autowired
	CreditCardService creditCardsService;

	/**
	 * Requests a new credit card. 
	 *
	 * @param creditCardRequest The credit card request payload
	 * @return ApiResponse containing the credit card response
	 */
	@Override
	public ApiResponse<CreditCardResponse> requestCreditCard(CreditCardRequest creditCardRequest) {
		logger.debug("Received request to create credit card: {}", creditCardRequest);
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.requestCreditCard(creditCardRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit card created successfully");
		response.setData(creditCardResponse);
		logger.info("Credit card created successfully: {}", creditCardResponse);
		return response;
	}

	/**
	 * Checks the debt of a credit card. 
	 *
	 * @param creditCardId The credit card ID
	 * @return ApiResponse containing the credit card debt response
	 */
	@Override
	public ApiResponse<CreditCardDebtResponse> checkDebt(String creditCardId) {
		logger.debug("Received request to check debt for credit card: {}", creditCardId);
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebt(creditCardId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Check debt successfully");
		response.setData(creditCardDebtResponse);
		logger.info("Debt checked successfully for credit card: {}", creditCardId);
		return response;
	}

	/**
	 * Finds all credit cards. 
	 *
	 * @return ApiResponse containing the list of credit card responses
	 */
	@Override
	public ApiResponse<List<CreditCardResponse>> findAllCreditCards() {
		logger.debug("Received request to find all credit cards");
		ApiResponse<List<CreditCardResponse>> response = new ApiResponse<>();
		List<CreditCardResponse> creditCards = creditCardsService.findAllCreditCards();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit cards retrieved successfully");
		response.setData(creditCards);
		logger.info("All credit cards retrieved successfully");
		return response;
	}

	/**
	 * Updates a credit card.
	 *
	 * @param creditCardId The credit card ID
	 * @return ApiResponse containing the updated credit card response
	 */
	@Override
	public ApiResponse<CreditCardResponse> updateCreditCard(String creditCardId) {
		logger.debug("Received request to update credit card with ID: {}", creditCardId);
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.updateCreditCard(creditCardId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit card updated successfully");
		response.setData(creditCardResponse);
		logger.info("Credit card updated successfully with ID: {}", creditCardId);
		return response;
	}

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardId The credit card ID
	 * @return ApiResponse indicating the result of the delete operation
	 */
	@Override
	public ApiResponse<Void> deleteCreditCard(String creditCardId) {
		logger.debug("Received request to delete credit card with ID: {}", creditCardId);
		ApiResponse<Void> response = new ApiResponse<>();
		creditCardsService.deleteCreditCard(creditCardId);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Credit card deleted successfully");
		logger.info("Credit card deleted successfully with ID: {}", creditCardId);
		return response;
	}
}
