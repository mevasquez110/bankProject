package com.nttdata.bank.client;

import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;

public interface TransactionClient {

	TransactionResponse makeDeposit(TransactionRequest transactionRequest);
}
