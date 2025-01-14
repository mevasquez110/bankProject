package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;

/**
 * CreditService provides the service layer for handling credit-related
 * operations. This interface defines methods for granting a credit, checking
 * credit debt, updating reprogrammed debt, finding all credits, and deleting a
 * credit.
 */

public interface CreditService {

	/**
	 * Grants a new credit.
	 *
	 * @param creditRequest The credit request to create a new credit
	 * @return The response containing credit details
	 */
	CreditResponse grantCredit(CreditRequest creditRequest);

	/**
	 * Checks the debt of a credit.
	 *
	 * @param creditId The ID of the credit to check the debt for
	 * @return The response containing credit debt details
	 */
	CreditDebtResponse checkDebtCredit(String creditId);

	/**
	 * Finds all credits.
	 *
	 * @return A list of responses containing credit details
	 */
	List<CreditResponse> findAllCredits();

}
