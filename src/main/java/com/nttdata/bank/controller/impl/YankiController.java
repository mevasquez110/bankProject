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

/**
 * YankiController is a REST controller that implements the YankiAPI interface.
 * This class handles HTTP requests related to Yanki operations such as
 * creating, updating, deleting, and retrieving Yanki entities. It delegates the
 * actual business logic to the YankiService.
 */
@RestController
public class YankiController implements YankiAPI {

	private static final Logger logger = LoggerFactory.getLogger(YankiController.class);

	@Autowired
	private YankiService yankiService;

	/**
	 * Creates a new Yanki entity based on the provided YankiRequest object.
	 *
	 * @param yankiRequest - The Yanki details provided in the request body.
	 * @return ApiResponse containing the created YankiResponse.
	 */
	@Override
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
	 * Deletes the specified Yanki entity based on the phone number.
	 *
	 * @param phoneNumber - The phone number of the Yanki entity to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@Override
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
	 * Retrieves a list of all Yanki entities.
	 *
	 * @return ApiResponse containing a list of YankiResponse objects.
	 */
	@Override
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
	 * Updates the specified Yanki entity based on its phone number.
	 *
	 * @param phoneNumber        - The phone number of the Yanki entity to be
	 *                           updated.
	 * @param yankiUpdateRequest - The updated Yanki details provided in the request
	 *                           body.
	 * @return ApiResponse containing the updated YankiResponse.
	 */
	@Override
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
}
