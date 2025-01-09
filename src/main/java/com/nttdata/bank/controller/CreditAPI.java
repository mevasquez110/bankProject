package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.ReprogramDebtRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import java.util.List;

/**
 * CreditAPI defines the RESTful endpoints for credit-related operations. This
 * interface includes methods for granting credits, checking credit debt,
 * reprogramming debt, retrieving all credits, and deleting credits. Each method
 * maps to an HTTP request and returns a structured API response.
 */

@RestController
@RequestMapping("/credits")
public interface CreditAPI {

	/**
	 * Grants a new credit based on the provided CreditRequest object.
	 * 
	 * @param creditRequest - The credit details provided in the request body.
	 * @return ApiResponse containing the granted CreditResponse.
	 */
	@PostMapping("/grant")
	ApiResponse<CreditResponse> grantCredit(@RequestBody @Valid CreditRequest creditRequest);

	/**
	 * Checks the debt for the specified credit.
	 * 
	 * @param creditId - The ID of the credit for which the debt is to be checked.
	 * @return ApiResponse containing the CreditDebtResponse.
	 */
	@GetMapping("/debt/{creditId}")
	ApiResponse<CreditDebtResponse> checkDebt(@PathVariable String creditId);

	/**
	 * Updates the reprogrammed debt based on the provided ReprogramDebtRequest
	 * object.
	 * 
	 * @param reprogramDebtRequest - The reprogrammed debt details provided in the
	 *                             request body.
	 * @return ApiResponse containing the updated CreditResponse.
	 */
	@PutMapping("/reprogram")
	ApiResponse<CreditResponse> updateReprogramDebt(@RequestBody @Valid ReprogramDebtRequest reprogramDebtRequest);

	/**
	 * Retrieves a list of all credits.
	 * 
	 * @return ApiResponse containing a list of CreditResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<CreditResponse>> findAllCredits();

	/**
	 * Deletes the specified credit based on the credit ID.
	 * 
	 * @param creditId - The ID of the credit to be deleted.
	 * @return ApiResponse with no content upon successful deletion.
	 */
	@DeleteMapping("/{creditId}")
	ApiResponse<Void> deleteCredit(@PathVariable String creditId);
}
