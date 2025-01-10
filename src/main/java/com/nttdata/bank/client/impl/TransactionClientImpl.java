package com.nttdata.bank.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.nttdata.bank.client.TransactionClient;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.ApiResponse;
import com.nttdata.bank.response.TransactionResponse;
import reactor.core.publisher.Mono;

/**
 * Implementation of the TransactionClient interface for handling
 * transaction-related operations. This class uses WebClient to make HTTP
 * requests to the transaction service.
 */
@Service
public class TransactionClientImpl implements TransactionClient {

	private static final Logger logger = LoggerFactory.getLogger(TransactionClientImpl.class);

	@Autowired
	private WebClient.Builder webClientBuilder;

	/**
	 * Makes a deposit transaction by sending a POST request to the transaction
	 * service. This method uses WebClient to send the request and handles the
	 * response asynchronously.
	 * 
	 * @param transactionRequest The details of the transaction to be made.
	 * @return The response from the transaction service containing transaction
	 *         details.
	 * @throws IllegalArgumentException if the transaction response or its data is
	 *                                  null.
	 */
	@Override
	public TransactionResponse makeDeposit(TransactionRequest transactionRequest) {
		Mono<TransactionResponse> transactionResponseMono = webClientBuilder.build().post()
				.uri("http://localhost:8080/transactions/deposit")
				.body(Mono.just(transactionRequest), TransactionRequest.class).retrieve()
				.bodyToMono(new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {
				}).map(ApiResponse::getData).doOnError(error -> {
					logger.error("Error occurred while making deposit: {}", error.getMessage());
					throw new IllegalArgumentException("Transaction response or its data is null.", error);
				});
		return transactionResponseMono.block();
	}

}
