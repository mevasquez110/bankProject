package com.nttdata.bank.service;

import java.util.List;
import javax.validation.Valid;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.response.ConsumptionResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.CreditCardResponse;

/**
 * CreditCardService provides the service layer for handling credit card-related
 * operations. This interface defines methods for requesting a credit card,
 * checking credit card debt, finding all credit cards, updating a credit card,
 * deleting a credit card, and managing credit card consumptions.
 */

public interface CreditCardService {

	/**
	 * Requests a new credit card.
	 *
	 * @param creditCardRequest The request containing details necessary to create a
	 *                          new credit card
	 * @return The response containing detailed information about the created credit
	 *         card
	 */
	CreditCardResponse requestCreditCard(CreditCardRequest creditCardRequest);

	/**
	 * Checks the debt of a credit card.
	 *
	 * @param creditCardNumber The number of the credit card to check the debt for
	 * @return The response containing detailed information about the credit card
	 *         debt
	 */
	CreditCardDebtResponse checkDebtCreditCard(String creditCardNumber);

	/**
	 * Finds all credit cards.
	 *
	 * @return A list of responses containing detailed information about all credit
	 *         cards
	 */
	List<CreditCardResponse> findAllCreditCards();

	/**
	 * Updates an existing credit card.
	 *
	 * @param creditCardNumber The number of the credit card to update
	 * @return The response containing detailed information about the updated credit
	 *         card
	 */
	CreditCardResponse updateCreditCard(String creditCardNumber);

	/**
	 * Deletes a credit card.
	 *
	 * @param creditCardNumber The number of the credit card to delete
	 */
	void deleteCreditCard(String creditCardNumber);

	/**
	 * Charges a consumption to the credit card.
	 *
	 * @param consumptionRequest The request containing details about the
	 *                           consumption to be charged
	 * @return The response containing details about the charged consumption
	 */
	ConsumptionResponse chargeConsumption(@Valid ConsumptionRequest consumptionRequest);

	CreditCardEntity updateBalance(String creditCardNumber,
			Double balanceReturned);

}
