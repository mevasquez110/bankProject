package com.nttdata.bank.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.nttdata.bank.client.TransactionClient;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;

@Service
public class TransactionClientImpl implements TransactionClient {

	private static final Logger logger = LoggerFactory.getLogger(TransactionClientImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Override
	public TransactionResponse makeDeposit(TransactionRequest transactionRequest) {
		TransactionResponse response = new TransactionResponse();
		ApiResponse<TransactionResponse> transactionResponse = restTemplate
				.postForObject("http://localhost:8080/transactions/deposit", transactionRequest, ApiResponse.class);

		if (transactionResponse != null && transactionResponse.getData() != null) {
			response = transactionResponse.getData();
		} else {
			logger.error("Transaction response or its data is null. Cannot set amount.");
			throw new IllegalArgumentException("Transaction response or its data is null.");
		}
		
		return response;
	}

}
