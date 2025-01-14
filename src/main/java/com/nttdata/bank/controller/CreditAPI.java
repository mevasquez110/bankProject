package com.nttdata.bank.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditDebtResponse;
import com.nttdata.bank.response.CreditResponse;

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
	@GetMapping("/debt")
	ApiResponse<CreditDebtResponse> checkDebtCredit(@RequestParam String creditId);

	/**
	 * Retrieves a list of all credits.
	 * 
	 * @return ApiResponse containing a list of CreditResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<CreditResponse>> findAllCredits();

}
