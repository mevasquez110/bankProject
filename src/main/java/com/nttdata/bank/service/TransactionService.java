package com.nttdata.bank.service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;

public interface TransactionService {

	TransactionResponse makeDeposit(TransactionRequest transaction);

	TransactionResponse makeWithdrawal(TransactionRequest transaction);

	TransactionResponse payInstallment(TransactionRequest transaction);

	List<TransactionResponse> checkTransactions(@PathVariable Integer accountId);

	TransactionResponse chargeConsumption(TransactionRequest transaction);
}
