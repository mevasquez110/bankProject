package com.nttdata.bank.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.response.DebitCardResponse;
import com.nttdata.bank.service.DebitCardService;

/**
 * Implementation of the DebitCardService interface. This class provides methods
 * for creating, finding, checking balance, associating accounts, marking
 * primary accounts, and deleting debit cards.
 */
@Service
public class DebitCardServiceImpl implements DebitCardService {

	/**
	 * Creates a new debit card based on the provided request.
	 *
	 * @param debitCardRequest the debit card request containing the details for
	 *                         creating the debit card
	 * @return DebitCardResponse containing the details of the created debit card
	 */
	@Override
	public DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Retrieves all debit cards.
	 *
	 * @return List of DebitCardResponse containing details of all debit cards
	 */
	@Override
	public List<DebitCardResponse> findAllDebitCard() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Checks the balance of a debit card based on the provided debit card number.
	 *
	 * @param debitCardNumber the debit card number for which to check the balance
	 * @return BalanceResponse containing the balance details
	 */
	@Override
	public BalanceResponse checkBalance(String debitCardNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Associates an account with a debit card based on the provided request.
	 *
	 * @param debitCardNumber         the debit card number
	 * @param associateAccountRequest the request containing details for associating
	 *                                the account
	 * @return DebitCardResponse containing the updated debit card details
	 */
	@Override
	public DebitCardResponse associateAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Marks an associated account as the primary account for a debit card.
	 *
	 * @param debitCardNumber         the debit card number
	 * @param associateAccountRequest the request containing details for marking the
	 *                                primary account
	 * @return DebitCardResponse containing the updated debit card details
	 */
	@Override
	public DebitCardResponse markAsPrimaryAccount(String debitCardNumber,
			AssociateAccountRequest associateAccountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Deletes a debit card based on the provided debit card number.
	 *
	 * @param debitCardNumber the debit card number to delete
	 */
	@Override
	public void deleteDebitCard(String debitCardNumber) {
		// TODO Auto-generated method stub
	}

}
