package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.DebitCardResponse;

public interface DebitCardService {

	DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest);

	List<DebitCardResponse> findAllDebitCard();

	DebitCardResponse associateAccount(String documentNumber);

	DebitCardResponse markAsPrimaryAccount(String documentNumber);

	void deleteDebitCard(String phoneNumber);
}
