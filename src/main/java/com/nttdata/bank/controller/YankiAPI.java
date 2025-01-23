package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.YankiResponse;

/**
 * YankiAPI defines the RESTful endpoints for Yanki-related operations. This
 * interface includes methods for creating, retrieving, updating, and deleting
 * Yankis. Each method maps to an HTTP request and returns a structured API
 * response.
 */
@RestController
@RequestMapping("/yanki")
public interface YankiAPI {

	/**
	 * Creates a new Yanki based on the provided YankiRequest object.
	 *
	 * @param yankiRequest - The Yanki details provided in the request body.
	 * @return ApiResponse containing the created YankiResponse.
	 */
	@PostMapping("/create")
	ApiResponse<YankiResponse> createYanki(@RequestBody @Valid YankiRequest yankiRequest);

	/**
	 * Retrieves a list of all Yankis.
	 *
	 * @return ApiResponse containing a list of YankiResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<YankiResponse>> findAllYanki();

	/**
	 * Updates the specified Yanki based on the phone number and the provided
	 * YankiUpdateRequest object.
	 *
	 * @param phoneNumber        - The phone number of the Yanki to be updated.
	 * @param yankiUpdateRequest - The updated Yanki details provided in the request
	 *                           body.
	 * @return ApiResponse containing the updated YankiResponse.
	 */
	@PutMapping("/update/{phoneNumber}")
	ApiResponse<YankiResponse> updateYanki(@PathVariable String phoneNumber,
			@RequestBody @Valid YankiUpdateRequest yankiUpdateRequest);

	/**
	 * Deletes the specified Yanki based on the phone number.
	 *
	 * @param phoneNumber - The phone number of the Yanki to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@DeleteMapping("/delete/{phoneNumber}")
	ApiResponse<Void> deleteYanki(@PathVariable String phoneNumber);
}
