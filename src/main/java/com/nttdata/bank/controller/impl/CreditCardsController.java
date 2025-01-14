package com.nttdata.bank.controller.impl;

import java.util.List;
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
	 * @param creditCardNumber The credit card number
	 * @return ApiResponse containing the credit card debt response
	 */
	@Override
	public ApiResponse<CreditCardDebtResponse> checkDebtCreditCard(String creditCardNumber) {
		logger.debug("Received request to check debt for credit card: {}", creditCardNumber);
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebtCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Check debt successfully");
		response.setData(creditCardDebtResponse);
		logger.info("Debt checked successfully for credit card: {}", creditCardNumber);
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
	 * @param creditCardNumber The credit card number
	 * @return ApiResponse containing the updated credit card response
	 */
	@Override
	public ApiResponse<CreditCardResponse> updateCreditCard(String creditCardNumber) {
		logger.debug("Received request to update credit card number: {}", creditCardNumber);
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.updateCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit card updated successfully");
		response.setData(creditCardResponse);
		logger.info("Credit card updated successfully with credit card number: {}", creditCardNumber);
		return response;
	}

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardNumber The credit card number
	 * @return ApiResponse indicating the result of the delete operation
	 */
	@Override
	public ApiResponse<Void> deleteCreditCard(String creditCardNumber) {
		logger.debug("Received request to delete credit card number: {}", creditCardNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		creditCardsService.deleteCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Credit card deleted successfully");
		logger.info("Credit card deleted successfully with credit card number: {}", creditCardNumber);
		return response;
	}
}
