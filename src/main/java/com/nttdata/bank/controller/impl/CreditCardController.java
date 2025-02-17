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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * CreditCardController is a REST controller that implements the CreditCardsAPI
 * interface. This class handles HTTP requests related to credit card operations
 * such as requesting, checking debt, retrieving, updating, and deleting credit
 * cards. It delegates the actual business logic to the appropriate service
 * layer.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class CreditCardController implements CreditCardsAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardController.class);

	@Autowired
	CreditCardService creditCardsService;

	/**
	 * Requests a new credit card based on the provided CreditCardRequest object.
	 * Utilizes CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param creditCardRequest - The credit card details provided in the request
	 *                          body.
	 * @return ApiResponse containing the created CreditCardResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackRequestCreditCard")
	@TimeLimiter(name = "creditCardService")
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
	 * Checks the debt for the specified credit card. Utilizes CircuitBreaker and
	 * TimeLimiter to handle resilience.
	 *
	 * @param creditCardNumber - The credit card number for which the debt is to be
	 *                         checked.
	 * @return ApiResponse containing the CreditCardDebtResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackCheckDebtCreditCard")
	@TimeLimiter(name = "creditCardService")
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
	 * Retrieves a list of all credit cards. Utilizes CircuitBreaker and TimeLimiter
	 * to handle resilience.
	 *
	 * @return ApiResponse containing a list of CreditCardResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackFindAllCreditCards")
	@TimeLimiter(name = "creditCardService")
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
	 * Updates the specified credit card based on the credit card number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param creditCardNumber - The credit card number to be updated.
	 * @return ApiResponse containing the updated CreditCardResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackUpdateCreditCard")
	@TimeLimiter(name = "creditCardService")
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
	 * Deletes the specified credit card based on the credit card number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param creditCardNumber - The credit card number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackDeleteCreditCard")
	@TimeLimiter(name = "creditCardService")
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
	 * Charges a new consumption to the specified credit card. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param consumptionRequest - The details of the consumption to be charged.
	 * @return ApiResponse containing the ConsumptionResponse.
	 */
	@Override
	@CircuitBreaker(name = "creditCardService", fallbackMethod = "fallbackChargeConsumption")
	@TimeLimiter(name = "creditCardService")
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

	/**
	 * Fallback method for requestCreditCard in case of failure or timeout.
	 *
	 * @param creditCardRequest - The original credit card request.
	 * @param throwable         - The exception that caused the fallback to be
	 *                          triggered.
	 * @return ApiResponse indicating failure to create credit card.
	 */
	public ApiResponse<CreditCardResponse> fallbackRequestCreditCard(CreditCardRequest creditCardRequest,
			Throwable throwable) {
		logger.error("Fallback method for requestCreditCard due to: {}", throwable.getMessage());
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to create credit card at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for checkDebtCreditCard in case of failure or timeout.
	 *
	 * @param creditCardNumber - The credit card number being checked.
	 * @param throwable        - The exception that caused the fallback to be
	 *                         triggered.
	 * @return ApiResponse indicating failure to check debt.
	 */
	public ApiResponse<CreditCardDebtResponse> fallbackCheckDebtCreditCard(String creditCardNumber,
			Throwable throwable) {
		logger.error("Fallback method for checkDebtCreditCard due to: {}", throwable.getMessage());
		ApiResponse<CreditCardDebtResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to check credit card debt at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllCreditCards in case of failure or timeout.
	 *
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to retrieve credit cards.
	 */
	public ApiResponse<List<CreditCardResponse>> fallbackFindAllCreditCards(Throwable throwable) {
		logger.error("Fallback method for findAllCreditCards due to: {}", throwable.getMessage());
		ApiResponse<List<CreditCardResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve credit cards at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for updateCreditCard in case of failure or timeout.
	 *
	 * @param creditCardNumber - The credit card number being updated.
	 * @param throwable        - The exception that caused the fallback to be
	 *                         triggered.
	 * @return ApiResponse indicating failure to update credit card.
	 */
	public ApiResponse<CreditCardResponse> fallbackUpdateCreditCard(String creditCardNumber, Throwable throwable) {
		logger.error("Fallback method for updateCreditCard due to: {}", throwable.getMessage());
		ApiResponse<CreditCardResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to update credit card at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for deleteCreditCard in case of failure or timeout.
	 *
	 * @param creditCardNumber - The credit card number being deleted.
	 * @param throwable        - The exception that caused the fallback to be
	 *                         triggered.
	 * @return ApiResponse indicating failure to delete credit card.
	 */
	public ApiResponse<Void> fallbackDeleteCreditCard(String creditCardNumber, Throwable throwable) {
		logger.error("Fallback method for deleteCreditCard due to: {}", throwable.getMessage());
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to delete credit card at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for chargeConsumption in case of failure or timeout.
	 *
	 * @param consumptionRequest - The original consumption request.
	 * @param throwable          - The exception that caused the fallback to be
	 *                           triggered.
	 * @return ApiResponse indicating failure to charge consumption.
	 */
	public ApiResponse<ConsumptionResponse> fallbackChargeConsumption(ConsumptionRequest consumptionRequest,
			Throwable throwable) {
		logger.error("Fallback method for chargeConsumption due to: {}", throwable.getMessage());
		ApiResponse<ConsumptionResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to charge consumption at the moment. Please try again later.");
		return response;
	}
}
