package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

/**
 * CreditCardService provides the service layer for handling credit card-related
 * operations. This interface defines methods for requesting a credit card,
 * checking credit card debt, finding all credit cards, updating a credit card,
 * and deleting a credit card.
 */

public interface CreditCardService {

	/**
	 * Requests a new credit card.
	 *
	 * @param creditCardRequest The credit card request to create a new credit card
	 * @return The response containing credit card details
	 */
	CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest);

	/**
	 * Checks the debt of a credit card.
	 *
	 * @param creditCardNumber The credit card number to check the debt for
	 * @return The response containing credit card debt details
	 */
	CreditCardDebtResponse checkDebtCreditCard(String creditCardNumber);

	/**
	 * Finds all credit cards.
	 *
	 * @return A list of responses containing credit card details
	 */
	List<CreditCardResponse> findAllCreditCards();

	/**
	 * Updates an existing credit card.
	 *
	 * @param creditCardNumber The credit card number to update
	 * @return The response containing updated credit card details
	 */
	CreditCardResponse updateCreditCard(String creditCardNumber);

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardNumber The credit card number to delete
	 */
	void deleteCreditCard(String creditCardNumber);
}
