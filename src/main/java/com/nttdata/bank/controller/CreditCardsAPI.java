package com.nttdata.bank.controller;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;
import java.util.List;

/**
 * CreditCardsAPI defines the RESTful endpoints for credit card-related
 * operations. This interface includes methods for requesting credit cards,
 * checking credit card debt, retrieving all credit cards, updating credit
 * cards, and deleting credit cards. Each method maps to an HTTP request and
 * returns a structured API response.
 */

@RestController
@RequestMapping("/credit-cards")
public interface CreditCardsAPI {

	/**
	 * Requests a new credit card based on the provided CreditCardRequest object.
	 * 
	 * @param creditCardRequest - The credit card details provided in the request
	 *                          body.
	 * @return ApiResponse containing the requested CreditCardResponse.
	 */
	@PostMapping("/request")
	ApiResponse<CreditCardResponse> requestCreditCard(@RequestBody @Valid CreditCardRequest creditCardRequest);

	/**
	 * Checks the debt for the specified credit card.
	 * 
	 * @param creditCardNumber - The credit card number for which the debt is to be
	 *                     checked.
	 * @return ApiResponse containing the CreditCardDebtResponse.
	 */
	@GetMapping("/debt")
	ApiResponse<CreditCardDebtResponse> checkDebt(@RequestParam String creditCardNumber);

	/**
	 * Retrieves a list of all credit cards.
	 * 
	 * @return ApiResponse containing a list of CreditCardResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<CreditCardResponse>> findAllCreditCards();

	/**
	 * Updates the specified credit card based on the credit card ID.
	 * 
	 * @param creditCardNumber - The credit card number to be updated.
	 * @return ApiResponse containing the updated CreditCardResponse.
	 */
	@PutMapping("/update/{creditCardNumber}")
	ApiResponse<CreditCardResponse> updateCreditCard(@PathVariable String creditCardNumber);

	/**
	 * Deletes the specified credit card based on the credit card ID.
	 * 
	 * @param creditCardNumber - The credit card number to be deleted.
	 * @return ApiResponse with no content upon successful deletion.
	 */
	@DeleteMapping("/delete/{creditCardNumber}")
	ApiResponse<Void> deleteCreditCard(@PathVariable String creditCardNumber);
}
