package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.PaymentScheduleEntity;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.mapper.TransactionMapper;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.PaymentScheduleRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.TransactionService;
import com.nttdata.bank.util.Utility;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PaymentScheduleRepository paymentScheduleRepository;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Override
	public TransactionResponse makeDeposit(TransactionRequest transaction) {
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("DEPOSIT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);
		return TransactionMapper.mapperToResponse(savedTransaction);
	}

	@Override
	public TransactionResponse makeWithdrawal(TransactionRequest transaction) {
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("WITHDRAWAL");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);
		return TransactionMapper.mapperToResponse(savedTransaction);
	}

	@Override
	public TransactionResponse payInstallment(TransactionRequest transaction) {
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("CREDIT_PAYMENT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);
		return TransactionMapper.mapperToResponse(savedTransaction);
	}

	@Override
	public List<TransactionResponse> checkTransactions(Integer accountId) {
		List<TransactionEntity> transactions = transactionRepository.findByAccountNumber(accountId.toString());
		List<TransactionResponse> transactionResponses = new ArrayList<>();
		for (TransactionEntity transaction : transactions) {
			transactionResponses.add(TransactionMapper.mapperToResponse(transaction));
		}
		return transactionResponses;
	}

	@Override
	public TransactionResponse chargeConsumption(TransactionRequest transactionRequest) {
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionRequest);
		transactionEntity.setTransactionType("CREDIT_CARD_PAYMENT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);
		generatePaymentScheduleForConsumption(transactionEntity);
		return TransactionMapper.mapperToResponse(savedTransaction);
	}

	private void generatePaymentScheduleForConsumption(TransactionEntity transaction) {
		CreditCardEntity creditCardEntity = creditCardRepository.findById(transaction.getCreditCardNumber())
				.orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada"));

		List<PaymentScheduleEntity> schedule = new ArrayList<>();
		LocalDate firstPaymentDate = LocalDate.now().withDayOfMonth(transaction.getCreateDate().getDayOfMonth());
		Double monthlyInterestRate = Utility.getMonthlyInterestRate(creditCardEntity.getAnnualInterestRate());
		Double fixedInstallment = Utility.calculateInstallmentAmount(transaction.getAmount(), monthlyInterestRate, 12);
		Double remainingPrincipal = transaction.getAmount();

		for (int i = 1; i <= 12; i++) {
			PaymentScheduleEntity payment = new PaymentScheduleEntity();
			payment.setPaymentDate(firstPaymentDate.plusMonths(i - 1));
			Double interestPayment = remainingPrincipal * monthlyInterestRate;
			payment.setDebtAmount(remainingPrincipal - (fixedInstallment - interestPayment));
			payment.setSharePayment(fixedInstallment);
			payment.setCreditCardNumber(transaction.getCreditCardNumber());
			payment.setPaid(false);
			remainingPrincipal -= fixedInstallment - interestPayment;
			schedule.add(payment);
		}

		paymentScheduleRepository.saveAll(schedule);
	}
}
