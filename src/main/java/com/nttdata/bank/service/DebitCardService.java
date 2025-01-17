package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.DebitCardResponse;

/**
 * DebitCardService is the interface that provides methods for handling 
 * debit card-related operations. This includes creating debit cards, 
 * finding all debit cards, checking balances, associating accounts, 
 * marking primary accounts, and deleting debit cards.
 */
public interface DebitCardService {

    /**
     * Creates a new debit card based on the provided request.
     *
     * @param debitCardRequest the debit card request containing the details for creating the debit card
     * @return DebitCardResponse containing the details of the created debit card
     */
    DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest);

    /**
     * Retrieves all debit cards.
     *
     * @return List of DebitCardResponse containing details of all debit cards
     */
    List<DebitCardResponse> findAllDebitCard();

    /**
     * Associates an account with a debit card based on the provided request.
     *
     * @param debitCardNumber the debit card number
     * @param associateAccountRequest the request containing details for associating the account
     * @return DebitCardResponse containing the updated debit card details
     */
    DebitCardResponse associateAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest);

    /**
     * Deletes a debit card based on the provided debit card number.
     *
     * @param debitCardNumber the debit card number to delete
     */
    void deleteDebitCard(String debitCardNumber);
}
