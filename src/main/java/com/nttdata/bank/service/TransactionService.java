package com.nttdata.bank.service;

import java.util.List;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.TransactionResponse;


public interface TransactionService {
	
	TransactionResponse makeDeposit(DepositRequest depositRequest);

	TransactionResponse makeWithdrawal(WithdrawalRequest withdrawalRequest);

	TransactionResponse makeAccountTransfer(AccountTransferRequest accountTransferRequest);

	TransactionResponse makeMobileTransfer(MobileTransferRequest mobileTransferRequest);

	TransactionResponse payCreditCard(PayCreditCardRequest payCreditCardRequest);

	TransactionResponse payCredit(PayCreditRequest payCreditRequest);

	TransactionResponse chargeConsumption(ConsumptionRequest consumptionRequest);

	List<TransactionResponse> checkTransactions(Integer accountId);
}
