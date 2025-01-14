package com.nttdata.bank.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.response.DebitCardResponse;
import com.nttdata.bank.service.DebitCardService;

@Service
public class DebitCardServiceImpl implements DebitCardService {

	@Override
	public DebitCardResponse createDebitCard(DebitCardRequest debitCardRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DebitCardResponse> findAllDebitCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BalanceResponse checkBalance(String debitCardNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DebitCardResponse associateAccount(String debitCardNumber, AssociateAccountRequest associateAccountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DebitCardResponse markAsPrimaryAccount(String debitCardNumber,
			AssociateAccountRequest associateAccountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDebitCard(String debitCardNumber) {
		// TODO Auto-generated method stub

	}

}
