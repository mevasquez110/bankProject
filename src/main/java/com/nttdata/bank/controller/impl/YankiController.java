package com.nttdata.bank.controller.impl;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.YankiAPI;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.YankiResponse;
import com.nttdata.bank.service.YankiService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

/**
 * YankiController is a REST controller that implements the YankiAPI interface.
 * This class handles HTTP requests related to Yanki operations such as
 * creating, updating, deleting, and retrieving Yanki entities. It delegates the
 * actual business logic to the YankiService.
 * 
 * It also uses Resilience4j annotations (@CircuitBreaker and @TimeLimiter) to
 * provide resilience in case of failures or timeouts, and includes fallback
 * methods to handle these scenarios gracefully.
 */
@RestController
public class YankiController implements YankiAPI {

	private static final Logger logger = LoggerFactory.getLogger(YankiController.class);

	@Autowired
	private YankiService yankiService;

	/**
	 * Creates a new Yanki entity based on the provided YankiRequest object.
	 * Utilizes CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param yankiRequest - The Yanki details provided in the request body.
	 * @return ApiResponse containing the created YankiResponse.
	 */
	@Override
	@CircuitBreaker(name = "yankiService", fallbackMethod = "fallbackCreateYanki")
	@TimeLimiter(name = "yankiService")
	public ApiResponse<YankiResponse> createYanki(@Valid YankiRequest yankiRequest) {
		logger.debug("Received request to create Yanki: {}", yankiRequest);
		ApiResponse<YankiResponse> response = new ApiResponse<>();
		YankiResponse yankiResponse = yankiService.createYanki(yankiRequest);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Yanki successfully created.");
		response.setData(yankiResponse);
		logger.info("Yanki created successfully: {}", yankiResponse);
		return response;
	}

	/**
	 * Deletes the specified Yanki entity based on the phone number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param phoneNumber - The phone number of the Yanki entity to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
	@CircuitBreaker(name = "yankiService", fallbackMethod = "fallbackDeleteYanki")
	@TimeLimiter(name = "yankiService")
	public ApiResponse<Void> deleteYanki(String phoneNumber) {
		logger.debug("Received request to delete Yanki with phone number: {}", phoneNumber);
		ApiResponse<Void> response = new ApiResponse<>();
		yankiService.deleteYanki(phoneNumber);
		response.setStatusCode(HttpStatus.NO_CONTENT.value());
		response.setMessage("Yanki successfully deleted.");
		logger.info("Yanki deleted successfully with phone number: {}", phoneNumber);
		return response;
	}

	/**
	 * Retrieves a list of all Yanki entities. Utilizes CircuitBreaker and
	 * TimeLimiter to handle resilience.
	 *
	 * @return ApiResponse containing a list of YankiResponse objects.
	 */
	@Override
	@CircuitBreaker(name = "yankiService", fallbackMethod = "fallbackFindAllYanki")
	@TimeLimiter(name = "yankiService")
	public ApiResponse<List<YankiResponse>> findAllYanki() {
		logger.debug("Received request to find all Yanki entities.");
		ApiResponse<List<YankiResponse>> response = new ApiResponse<>();
		List<YankiResponse> yankis = yankiService.findAllYanki();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Yanki entities retrieved successfully.");
		response.setData(yankis);
		logger.info("Yanki entities retrieved successfully.");
		return response;
	}

	/**
	 * Updates the specified Yanki entity based on its phone number. Utilizes
	 * CircuitBreaker and TimeLimiter to handle resilience.
	 *
	 * @param phoneNumber        - The phone number of the Yanki entity to be
	 *                           updated.
	 * @param yankiUpdateRequest - The updated Yanki details provided in the request
	 *                           body.
	 * @return ApiResponse containing the updated YankiResponse.
	 */
	@Override
	@CircuitBreaker(name = "yankiService", fallbackMethod = "fallbackUpdateYanki")
	@TimeLimiter(name = "yankiService")
	public ApiResponse<YankiResponse> updateYanki(String phoneNumber, @Valid YankiUpdateRequest yankiUpdateRequest) {
		logger.debug("Received request to update Yanki with phone number: {}", phoneNumber);
		ApiResponse<YankiResponse> response = new ApiResponse<>();
		YankiResponse yankiResponse = yankiService.updateYanki(phoneNumber, yankiUpdateRequest);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Yanki successfully updated.");
		response.setData(yankiResponse);
		logger.info("Yanki updated successfully: {}", yankiResponse);
		return response;
	}

	/**
	 * Fallback method for createYanki in case of failure or timeout.
	 *
	 * @param yankiRequest - The original Yanki request.
	 * @param throwable    - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to create Yanki.
	 */
	public ApiResponse<YankiResponse> fallbackCreateYanki(YankiRequest yankiRequest, Throwable throwable) {
		logger.error("Fallback method for createYanki due to: {}", throwable.getMessage());
		ApiResponse<YankiResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to create Yanki at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for deleteYanki in case of failure or timeout.
	 *
	 * @param phoneNumber - The phone number of the Yanki being deleted.
	 * @param throwable   - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to delete Yanki.
	 */
	public ApiResponse<Void> fallbackDeleteYanki(String phoneNumber, Throwable throwable) {
		logger.error("Fallback method for deleteYanki due to: {}", throwable.getMessage());
		ApiResponse<Void> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to delete Yanki at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for findAllYanki in case of failure or timeout.
	 *
	 * @param throwable - The exception that caused the fallback to be triggered.
	 * @return ApiResponse indicating failure to retrieve Yanki entities.
	 */
	public ApiResponse<List<YankiResponse>> fallbackFindAllYanki(Throwable throwable) {
		logger.error("Fallback method for findAllYanki due to: {}", throwable.getMessage());
		ApiResponse<List<YankiResponse>> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to retrieve Yanki entities at the moment. Please try again later.");
		return response;
	}

	/**
	 * Fallback method for updateYanki in case of failure or timeout.
	 *
	 * @param phoneNumber        - The phone number of the Yanki being updated.
	 * @param yankiUpdateRequest - The updated Yanki request.
	 * @param throwable          - The exception that caused the fallback to be
	 *                           triggered.
	 * @return ApiResponse indicating failure to update Yanki.
	 */
	public ApiResponse<YankiResponse> fallbackUpdateYanki(String phoneNumber, YankiUpdateRequest yankiUpdateRequest,
			Throwable throwable) {
		logger.error("Fallback method for updateYanki due to: {}", throwable.getMessage());
		ApiResponse<YankiResponse> response = new ApiResponse<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Unable to update Yanki at the moment. Please try again later.");
		return response;
	}
}
