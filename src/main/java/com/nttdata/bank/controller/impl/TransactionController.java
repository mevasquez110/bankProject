package com.nttdata.bank.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeDeposit(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Deposit made successfully");
		response.setData(transactionResponse);
		return response;
	}

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> makeWithdrawal(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.makeWithdrawal(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Withdrawal made successfully");
		response.setData(transactionResponse);
		return response;
	}

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> payInstallment(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.payInstallment(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Installment paid successfully");
		response.setData(transactionResponse);
		return response;
	}

	@Override
	@Transactional
	public ApiResponse<List<TransactionResponse>> checkTransactions(Integer accountId) {
		ApiResponse<List<TransactionResponse>> response = new ApiResponse<>();
		List<TransactionResponse> transactionResponses = transactionService.checkTransactions(accountId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Transactions retrieved successfully");
		response.setData(transactionResponses);
		return response;
	}

	@Override
	@Transactional
	public ApiResponse<TransactionResponse> chargeConsumption(TransactionRequest transaction) {
		ApiResponse<TransactionResponse> response = new ApiResponse<>();
		TransactionResponse transactionResponse = transactionService.chargeConsumption(transaction);
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Consumption charged successfully");
		response.setData(transactionResponse);
		return response;
	}
}
