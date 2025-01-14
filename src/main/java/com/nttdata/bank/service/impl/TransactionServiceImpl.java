package com.nttdata.bank.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.TransactionService;

/**
 * * TransactionServiceImpl is the implementation class for the
 * TransactionService interface. * This class provides the actual logic for
 * handling transaction-related operations such as making deposits, * making
 * withdrawals, paying installments, checking transactions, and charging
 * consumption.
 */

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Override
	public TransactionResponse makeDeposit(DepositRequest depositRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse makeWithdrawal(WithdrawalRequest withdrawalRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse makeAccountTransfer(AccountTransferRequest accountTransferRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse makeMobileTransfer(MobileTransferRequest mobileTransferRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse payCreditCard(PayCreditCardRequest payCreditCardRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse payCredit(PayCreditRequest payCreditRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionResponse chargeConsumption(ConsumptionRequest consumptionRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionResponse> checkTransactions(Integer accountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
