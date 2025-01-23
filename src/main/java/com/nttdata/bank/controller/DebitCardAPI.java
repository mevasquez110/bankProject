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
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.DebitCardResponse;

/**
 * DebitCardAPI defines the RESTful endpoints for debit card-related operations.
 * This interface includes methods for creating debit cards, retrieving all
 * debit cards, associating accounts with debit cards, and deleting debit cards.
 * Each method maps to an HTTP request and returns a structured API response.
 */
@RestController
@RequestMapping("/debit-card")
public interface DebitCardAPI {

	/**
	 * Creates a new debit card based on the provided DebitCardRequest object.
	 *
	 * @param debitCardRequest - The debit card details provided in the request
	 *                         body.
	 * @return ApiResponse containing the created DebitCardResponse.
	 */
	@PostMapping("/create")
	ApiResponse<DebitCardResponse> createDebitCard(@RequestBody @Valid DebitCardRequest debitCardRequest);

	/**
	 * Retrieves a list of all debit cards.
	 *
	 * @return ApiResponse containing a list of DebitCardResponse objects.
	 */
	@GetMapping("/all")
	ApiResponse<List<DebitCardResponse>> findAllDebitCard();

	/**
	 * Associates an account with the specified debit card based on the provided
	 * AssociateAccountRequest object.
	 *
	 * @param debitCardNumber         - The debit card number to be associated with
	 *                                an account.
	 * @param associateAccountRequest - The association details provided in the
	 *                                request body.
	 * @return ApiResponse containing the updated DebitCardResponse.
	 */
	@PutMapping("/associate-account/{debitCardNumber}")
	ApiResponse<DebitCardResponse> associateAccount(@PathVariable String debitCardNumber,
			@RequestBody @Valid AssociateAccountRequest associateAccountRequest);

	/**
	 * Deletes the specified debit card based on the debit card number.
	 *
	 * @param debitCardNumber - The debit card number to be deleted.
	 * @return ApiResponse with a status message upon successful deletion.
	 */
	@DeleteMapping("/delete/{debitCardNumber}")
	ApiResponse<Void> deleteDebitCard(@PathVariable String debitCardNumber);
}
