package com.nttdata.bank.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bank.controller.TransactionAPI;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.TransactionService;

@RestController
public class TransactionController implements TransactionAPI {

	@Autowired
	TransactionService transactionService;

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> makeDeposit(TransactionRequest transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> makeWithdrawal(TransactionRequest transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> payInstallment(TransactionRequest transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ApiResponse<List<TransactionResponse>> checkTransactions(Integer accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse<TransactionResponse> chargeConsumption(TransactionRequest transaction) {
		// TODO Auto-generated method stub
		return null;
	}

}
