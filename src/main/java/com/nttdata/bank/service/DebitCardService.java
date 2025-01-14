package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.response.DebitCardResponse;

public interface DebitCardService {

	DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest);

	List<DebitCardResponse> findAllDebitCard();

	BalanceResponse checkBalance(String debitCardNumber);

	DebitCardResponse associateAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest);

	DebitCardResponse markAsPrimaryAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest);

	void deleteDebitCard(String debitCardNumber);
}
