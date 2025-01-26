package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.DebitCardAPI;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.DebitCardResponse;
import com.nttdata.bank.service.DebitCardService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * DebitCardController is a REST controller that implements the DebitCardAPI
 * interface. This class handles HTTP requests related to debit card operations
 * such as creating, finding, associating accounts, and deleting debit cards. It
 * delegates the actual business logic to DebitCardService.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */

@RestController
public class DebitCardController implements DebitCardAPI {

	private static final Logger logger = LoggerFactory.getLogger(DebitCardController.class);

	@Autowired
	DebitCardService debitCardService;

	/**
	 * Creates a new debit card based on the provided DebitCardRequest object.
	 * Utilizes CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param debitCardRequest - The debit card details provided in the request
	 *                         body.
	 * @return ApiResponse containing the created DebitCardResponse.
	 */
	@Override
	@CircuitBreaker(name = "debitCardService", fallbackMethod = "fallbackCreateDebitCard")
	@TimeLimiter(name = "debitCardService")
	public ApiResponse<DebitCardResponse> createDebitCard(@Valid DebitCardRequest debitCardRequest) {
		logger.debug("Received request to create debit card: {}", debitCardRequest);
		ApiResponse<DebitCardResponse> response = new ApiResponse<>();
		DebitCardResponse debitCardResponse = debitCardService.createDebitCard(debitCardRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Debit card successfully created.");
		response.setData(debitCardResponse);
		logger.info("Debit card created successfully: {}", debitCardResponse);
		return response;
	}

	/**
	 * Retrieves a list of all debit cards. Utilizes CircuitBreaker and TimeLimiter
	 * to handle resilience.
	 *
	 * @return ApiResponse containing a list of DebitCardResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "debitCardService", fallbackMethod = "fallbackFindAllDebitCard")
	@TimeLimiter(name = "debitCardService")
	public ApiResponse<List<DebitCardResponse>> findAllDebitCard() {
		logger.debug("Received request to find all debit cards.");
		ApiResponse<List<DebitCardResponse>> response = new ApiResponse<>();
		List<DebitCardResponse> debitCards = debitCardService.findAllDebitCard();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Debit cards retrieved successfully.");
		response.setData(debitCards);
		logger.info("All debit cards retrieved successfully.");
		return response;
	}

	/**
	 * Associates an account with the specified debit card based on the provided
	 * AssociateAccountRequest object. Utilizes CircuitBreaker and TimeLimiter to
	 * handle resilience.
	 *
	 * @param debitCardNumber         - The debit card number to be associated with
	 *                                an account.
	 * @param associateAccountRequest - The association details provided in the
	 *                                request body.
	 * @return ApiResponse containing the updated DebitCardResponse.
	 */
	@Override
	@CircuitBreaker(name = "debitCardService", fallbackMethod = "fallbackAssociateAccount")
	@TimeLimiter(name = "debitCardService")
	public ApiResponse<DebitCardResponse> associateAccount(String debitCardNumber,
			@Valid AssociateAccountRequest associateAccountRequest) {
		logger.debug("Received request to associate account with debit card: {}", debitCardNumber);
		ApiResponse<DebitCardResponse> response = new ApiResponse<>();
		DebitCardResponse debitCardResponse = debitCardService.associateAccount(debitCardNumber,
				associateAccountRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Account successfully associated.");
		response.setData(debitCardResponse);
		logger.info("Account associated successfully with debit card: {}", debitCardNumber);
		return response;
	}

	/**
	 * Deletes the specified debit card based on the debit card number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param debitCardNumber - The debit card number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	@CircuitBreaker(name = "debitCardService", fallbackMethod = "fallbackDeleteDebitCard")
	@TimeLimiter(name = "debitCardService")
	public ApiResponse<Void> deleteDebitCard(String debitCardNumber) {
		logger.debug("Received request to delete debit card with number: {}", debitCardNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		debitCardService.deleteDebitCard(debitCardNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Debit card successfully deleted.");
		logger.info("Debit card deleted successfully with number: {}", debitCardNumber);
		return response;
	}

	/**
	 * Fallback method for createDebitCard in case of failure or timeout.
	 *
	 * @param debitCardRequest - The original debit card request.
	 * @param throwable        - The exception that caused the fallback to be
	 *                         triggered.
	 * @return ApiResponse indicating failure to create debit card.
	 */
	public ApiResponse<DebitCardResponse> fallbackCreateDebitCard(DebitCardRequest debitCardRequest,
			Throwable throwable) {
		logger.error("Fallback method for createDebitCard due to: {}", throwable.getMessage());
		ApiResponse<DebitCardResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to create debit card at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllDebitCard in case of failure or timeout.
	 *
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to retrieve debit cards.
	 */
	public ApiResponse<List<DebitCardResponse>> fallbackFindAllDebitCard(Throwable throwable) {
		logger.error("Fallback method for findAllDebitCard due to: {}", throwable.getMessage());
		ApiResponse<List<DebitCardResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve debit cards at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for associateAccount in case of failure or timeout.
	 *
	 * @param debitCardNumber         - The debit card number being associated.
	 * @param associateAccountRequest - The association details.
	 * @param throwable               - The exception that caused the fallback to be
	 *                                triggered.
	 * @return ApiResponse indicating failure to associate account.
	 */
	public ApiResponse<DebitCardResponse> fallbackAssociateAccount(String debitCardNumber,
			AssociateAccountRequest associateAccountRequest, Throwable throwable) {
		logger.error("Fallback method for associateAccount due to: {}", throwable.getMessage());
		ApiResponse<DebitCardResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to associate account at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for deleteDebitCard in case of failure or timeout.
	 *
	 * @param debitCardNumber - The debit card number being deleted.
	 * @param throwable       - The exception that caused the fallback to be
	 *                        triggered.
	 * @return ApiResponse indicating failure to delete debit card.
	 */
	public ApiResponse<Void> fallbackDeleteDebitCard(String debitCardNumber, Throwable throwable) {
		logger.error("Fallback method for deleteDebitCard due to: {}", throwable.getMessage());
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to delete debit card at the moment. Please try again later.");
		return response;
	}
}
