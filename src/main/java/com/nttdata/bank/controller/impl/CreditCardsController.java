package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.CreditCardsAPI;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.ConsumptionResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.service.CreditCardService;

/**
 * CreditCardsController is a REST controller that implements the CreditCardsAPI
 * interface. This class handles HTTP requests related to credit card operations
 * such as requesting, checking debt, retrieving, updating, and deleting credit
 * cards. It delegates the actual business logic to the appropriate service
 * layer.
 */
@RestController
public class CreditCardsController implements CreditCardsAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardsController.class);

	@Autowired
	CreditCardService creditCardsService;

	/**
	 * Requests a new credit card based on the provided CreditCardRequest object.
	 *
	 * @param creditCardRequest - The credit card details provided in the request
	 *                          body.
	 * @return ApiResponse containing the created CreditCardResponse.
	 */
	@Override
	public ApiResponse<CreditCardResponse> requestCreditCard(CreditCardRequest creditCardRequest) {
		logger.debug("Received request to create credit card: {}", creditCardRequest);
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.requestCreditCard(creditCardRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Credit card successfully created.");
		response.setData(creditCardResponse);
		logger.info("Credit card created successfully: {}", creditCardResponse);
		return response;
	}

	/**
	 * Checks the debt for the specified credit card.
	 *
	 * @param creditCardNumber - The credit card number for which the debt is to be
	 *                         checked.
	 * @return ApiResponse containing the CreditCardDebtResponse.
	 */
	@Override
	public ApiResponse<CreditCardDebtResponse> checkDebtCreditCard(String creditCardNumber) {
		logger.debug("Received request to check debt for credit card: {}", creditCardNumber);
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		CreditCardDebtResponse creditCardDebtResponse = creditCardsService.checkDebtCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Debt checked successfully.");
		response.setData(creditCardDebtResponse);
		logger.info("Debt checked successfully for credit card: {}", creditCardNumber);
		return response;
	}

	/**
	 * Retrieves a list of all credit cards.
	 *
	 * @return ApiResponse containing a list of CreditCardResponse objects.
	 */
	@Override
	public ApiResponse<List<CreditCardResponse>> findAllCreditCards() {
		logger.debug("Received request to find all credit cards.");
		ApiResponse<List<CreditCardResponse>> response = new ApiResponse<>();
		List<CreditCardResponse> creditCards = creditCardsService.findAllCreditCards();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit cards retrieved successfully.");
		response.setData(creditCards);
		logger.info("All credit cards retrieved successfully.");
		return response;
	}

	/**
	 * Updates the specified credit card based on the credit card number.
	 *
	 * @param creditCardNumber  - The credit card number to be updated.
	 * @param creditCardRequest - The updated credit card details provided in the
	 *                          request body.
	 * @return ApiResponse containing the updated CreditCardResponse.
	 */
	@Override
	public ApiResponse<CreditCardResponse> updateCreditCard(String creditCardNumber) {
		logger.debug("Received request to update credit card number: {}", creditCardNumber);
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		CreditCardResponse creditCardResponse = creditCardsService.updateCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Credit card updated successfully.");
		response.setData(creditCardResponse);
		logger.info("Credit card updated successfully: {}", creditCardResponse);
		return response;
	}

	/**
	 * Deletes the specified credit card based on the credit card number.
	 *
	 * @param creditCardNumber - The credit card number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	public ApiResponse<Void> deleteCreditCard(String creditCardNumber) {
		logger.debug("Received request to delete credit card number: {}", creditCardNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		creditCardsService.deleteCreditCard(creditCardNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Credit card deleted successfully.");
		logger.info("Credit card deleted successfully: {}", creditCardNumber);
		return response;
	}

	/**
	 * Charges a new consumption to the specified credit card.
	 *
	 * @param consumptionRequest - The details of the consumption to be charged.
	 * @return ApiResponse containing the ConsumptionResponse.
	 */
	@Override
	public ApiResponse<ConsumptionResponse> chargeConsumption(@Valid ConsumptionRequest consumptionRequest) {
		logger.debug("Received request to charge consumption: {}", consumptionRequest);
		ApiResponse<ConsumptionResponse> response = new ApiResponse<>();
		ConsumptionResponse consumptionResponse = creditCardsService.chargeConsumption(consumptionRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Consumption successfully charged.");
		response.setData(consumptionResponse);
		logger.info("Consumption charged successfully: {}", consumptionResponse);
		return response;
	}
}
