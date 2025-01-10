package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public TransactionResponse makeDeposit(TransactionRequest transaction) {
		logger.debug("Making deposit transaction: {}", transaction);
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("DEPOSIT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity).block();
		TransactionResponse response = TransactionMapper.mapperToResponse(savedTransaction);
		logger.info("Deposit made successfully: {}", response);
		return response;
	}

	@Override
	public TransactionResponse makeWithdrawal(TransactionRequest transaction) {
		logger.debug("Making withdrawal transaction: {}", transaction);
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("WITHDRAWAL");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity).block();
		TransactionResponse response = TransactionMapper.mapperToResponse(savedTransaction);
		logger.info("Withdrawal made successfully: {}", response);
		return response;
	}

	@Override
	public TransactionResponse payInstallment(TransactionRequest transaction) {
		logger.debug("Paying installment transaction: {}", transaction);
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transaction);
		transactionEntity.setTransactionType("CREDIT_PAYMENT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity).block();
		TransactionResponse response = TransactionMapper.mapperToResponse(savedTransaction);
		logger.info("Installment paid successfully: {}", response);
		return response;
	}

	@Override
	public List<TransactionResponse> checkTransactions(Integer accountId) {
		logger.debug("Checking transactions for account ID: {}", accountId);

		List<TransactionEntity> transactions = transactionRepository
				.findByAccountNumberAndIsActiveTrue(accountId.toString()).collectList().block();

		List<TransactionResponse> transactionResponses = new ArrayList<>();
		
		for (TransactionEntity transaction : transactions) {
			transactionResponses.add(TransactionMapper.mapperToResponse(transaction));
		}
		
		logger.info("Transactions checked successfully for account ID: {}", accountId);
		return transactionResponses;
	}

	@Override
	public TransactionResponse chargeConsumption(TransactionRequest transactionRequest) {
		logger.debug("Charging consumption transaction: {}", transactionRequest);
		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionRequest);
		transactionEntity.setTransactionType("CREDIT_CARD_PAYMENT");
		TransactionEntity savedTransaction = transactionRepository.save(transactionEntity).block();
		generatePaymentScheduleForConsumption(transactionEntity);
		TransactionResponse response = TransactionMapper.mapperToResponse(savedTransaction);
		logger.info("Consumption charged successfully: {}", response);
		return response;
	}

	private void generatePaymentScheduleForConsumption(TransactionEntity transaction) {
		logger.debug("Generating payment schedule for consumption transaction: {}", transaction.getId());
		CreditCardEntity creditCardEntity = creditCardRepository.findById(transaction.getCreditCardNumber())
				.blockOptional().orElseThrow(() -> {
					logger.error("Credit card not found: {}", transaction.getCreditCardNumber());
					return new RuntimeException("Tarjeta de crédito no encontrada");
				});

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
		logger.info("Payment schedule generated successfully for consumption transaction: {}", transaction.getId());
	}
}
