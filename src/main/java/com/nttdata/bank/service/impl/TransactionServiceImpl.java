package com.nttdata.bank.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.entity.YankiEntity;
import com.nttdata.bank.mapper.TransactionMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.TransactionService;
import com.nttdata.bank.util.Constants;

/**
 * TransactionServiceImpl is the implementation class for the TransactionService
 * interface. This class provides the actual logic for handling
 * transaction-related operations such as making deposits, making withdrawals,
 * paying installments, checking transactions, and charging consumption.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private DebitCardRepository debitCardRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private YankiRepository yankiRepository;

	/**
	 * Makes a deposit based on the provided deposit request.
	 *
	 * @param depositRequest the deposit request containing the details for making a
	 *                       deposit
	 * @return TransactionResponse containing the details of the completed deposit
	 */
	@Override
	public TransactionResponse makeDeposit(DepositRequest depositRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();
		String primaryAccount = getPrimaryAccount(depositRequest);
		String transactionType = Constants.TRANSACTION_TYPE_DEPOSIT;
		Double commission = -getCommission(primaryAccount, transactionType);

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType,
						depositRequest.getAmount() + commission, primaryAccount, generateUniqueOperationNumber()))
				.toFuture().join();

		AccountEntity account = accountRepository.findByAccountNumberAndIsActiveTrue(primaryAccount).toFuture().join();

		account.setAmount(account.getAmount() + transactionEntity.getAmount());
		account.setUpdateDate(transactionDate);
		accountRepository.save(account);

		return TransactionMapper.mapperToResponse(transactionEntity.getOperationNumber(),
				getName(account.getHolderDoc()), null, null, transactionEntity.getAccountNumber(), null, null,
				transactionEntity.getAmount(), transactionEntity.getCommission(), transactionEntity.getCreateDate(),
				transactionEntity.getTransactionType());
	}

	/**
	 * Makes a withdrawal based on the provided withdrawal request.
	 *
	 * @param withdrawalRequest the withdrawal request containing the details for
	 *                          making a withdrawal
	 * @return TransactionResponse containing the details of the completed
	 *         withdrawal
	 */
	@Override
	public TransactionResponse makeWithdrawal(WithdrawalRequest withdrawalRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();

		DebitCardEntity debitCardEntity = debitCardRepository
				.findByDebitCardNumberAndIsActiveTrue(withdrawalRequest.getDebitCardNumber()).block();

		if (debitCardEntity == null) {
			throw new IllegalArgumentException("card does not exist");
		}

		String transactionType = Constants.TRANSACTION_TYPE_WITHDRAWAL;
		Double commission = 0.00;
		Double balance = 0.00;
		String primaryAccount = debitCardEntity.getPrimaryAccount();
		Double amount = -withdrawalRequest.getAmount();
		Boolean hasBalance = false;
		int i = 0;

		do {
			if (i > 0) {
				primaryAccount = debitCardEntity.getAssociatedAccounts().get(i);
			}

			commission = -getCommission(primaryAccount, transactionType);
			balance = -(amount + commission);

			hasBalance = accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(primaryAccount, balance)
					.block();

			i++;
		} while (!hasBalance && debitCardEntity.getAssociatedAccounts().size() > i);

		if (!hasBalance) {
			throw new IllegalArgumentException(
					"None of the associated accounts have enough balance to make the withdrawal.");
		}

		String operationNumber = generateUniqueOperationNumber();

		TransactionEntity transactionEntity = transactionRepository.save(TransactionMapper
				.mapperToEntity(transactionDate, commission, transactionType, amount, primaryAccount, operationNumber))
				.toFuture().join();

		AccountEntity account = accountRepository
				.findByAccountNumberAndIsActiveTrue(debitCardEntity.getPrimaryAccount()).toFuture().join();

		account.setAmount(account.getAmount() - balance);
		account.setUpdateDate(transactionDate);
		account = accountRepository.save(account).block();

		return TransactionMapper.mapperToResponse(transactionEntity.getOperationNumber(),
				getName(account.getHolderDoc()), null, null, transactionEntity.getAccountNumber(), null, null,
				transactionEntity.getAmount(), transactionEntity.getCommission(), transactionEntity.getCreateDate(),
				transactionEntity.getTransactionType());
	}

	/**
	 * Makes an account transfer based on the provided account transfer request.
	 *
	 * @param accountTransferRequest the account transfer request containing the
	 *                               details for making an account transfer
	 * @return TransactionResponse containing the details of the completed account
	 *         transfer
	 */
	@Override
	public TransactionResponse makeAccountTransfer(AccountTransferRequest accountTransferRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();
		Double commission = 0.00;
		String transactionType = Constants.TRANSACTION_TYPE_BANK_TRANSFER;
		Double amount = accountTransferRequest.getAmount();
		String accountNumberSender = accountTransferRequest.getAccountNumberSender();
		String accountNumberRecipient = accountTransferRequest.getAccountNumberRecipient();

		Boolean hasBalance = accountRepository
				.existsByAccountNumberAndAmountGreaterThanEqual(accountNumberSender, amount).block();

		if (hasBalance) {
			throw new IllegalArgumentException("The account does not have sufficient balance for the transfer.");
		}

		String operationNumber = generateUniqueOperationNumber();

		TransactionEntity transactionEntitySender = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType, -amount,
						accountNumberSender, operationNumber))
				.toFuture().join();

		AccountEntity accountSender = accountRepository.findByAccountNumberAndIsActiveTrue(accountNumberSender)
				.toFuture().join();

		accountSender.setAmount(accountSender.getAmount() - amount);
		accountSender.setUpdateDate(transactionDate);
		accountSender = accountRepository.save(accountSender).block();

		TransactionEntity transactionEntityRecipient = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType, amount,
						accountNumberRecipient, operationNumber))
				.toFuture().join();

		AccountEntity accountRecipient = accountRepository.findByAccountNumberAndIsActiveTrue(accountNumberSender)
				.toFuture().join();

		accountRecipient.setAmount(accountRecipient.getAmount() + amount);
		accountRecipient.setUpdateDate(transactionDate);
		accountRecipient = accountRepository.save(accountRecipient).block();

		return TransactionMapper.mapperToResponse(transactionEntityRecipient.getOperationNumber(), null,
				getName(accountSender.getHolderDoc()), getName(accountRecipient.getHolderDoc()), null,
				transactionEntitySender.getAccountNumber(), transactionEntityRecipient.getAccountNumber(),
				transactionEntityRecipient.getAmount(), transactionEntityRecipient.getCommission(),
				transactionEntityRecipient.getCreateDate(), transactionEntityRecipient.getTransactionType());
	}

	/**
	 * Makes a mobile transfer based on the provided mobile transfer request.
	 *
	 * @param mobileTransferRequest the mobile transfer request containing the
	 *                              details for making a mobile transfer
	 * @return TransactionResponse containing the details of the completed mobile
	 *         transfer
	 */
	@Override
	public TransactionResponse makeMobileTransfer(MobileTransferRequest mobileTransferRequest) {
		YankiEntity yankiSender = yankiRepository
				.findByPhoneNumberAndIsActiveTrue(mobileTransferRequest.getMobileNumberSender()).toFuture().join();

		if (yankiSender == null) {
			throw new IllegalArgumentException("yanki sender does not exist");
		}

		YankiEntity yankiRecipient = yankiRepository
				.findByPhoneNumberAndIsActiveTrue(mobileTransferRequest.getMobileNumberRecipient()).toFuture().join();

		if (yankiRecipient == null) {
			throw new IllegalArgumentException("yanki recipient does not exist");
		}

		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberSender(yankiSender.getAccountNumber());
		accountTransferRequest.setAccountNumberRecipient(yankiRecipient.getAccountNumber());
		accountTransferRequest.setAmount(mobileTransferRequest.getAmount());
		return makeAccountTransfer(accountTransferRequest);
	}

	/**
	 * Pays a credit card based on the provided pay credit card request.
	 *
	 * @param payCreditCardRequest the pay credit card request containing the
	 *                             details for paying the credit card
	 * @return TransactionResponse containing the details of the completed credit
	 *         card payment
	 */
	@Override
	public TransactionResponse payCreditCard(PayCreditCardRequest payCreditCardRequest) {
		return null;
	}

	/**
	 * Pays a credit based on the provided pay credit request.
	 *
	 * @param payCreditRequest the pay credit request containing the details for
	 *                         paying the credit
	 * @return TransactionResponse containing the details of the completed credit
	 *         payment
	 */
	@Override
	public TransactionResponse payCredit(PayCreditRequest payCreditRequest) {
		return null;
	}

	/**
	 * Charges a consumption based on the provided consumption request.
	 *
	 * @param consumptionRequest the consumption request containing the details for
	 *                           charging the consumption
	 * @return TransactionResponse containing the details of the completed
	 *         consumption charge
	 */
	@Override
	public TransactionResponse chargeConsumption(ConsumptionRequest consumptionRequest) {
		return null;
	}

	/**
	 * Checks the transactions for the provided account ID.
	 *
	 * @param accountId the account ID for which to check the transactions
	 * @return List of TransactionResponse containing the details of the
	 *         transactions
	 */
	@Override
	public List<TransactionResponse> checkTransactions(Integer accountId) {
		return null;
	}

	private String generateUniqueOperationNumber() {
		return transactionRepository.findFirstByOrderByOperationNumberDesc().map(transaction -> {
			int newOperationNumber = Integer.parseInt(transaction.getOperationNumber()) + 1;
			return String.format("%012d", newOperationNumber);
		}).defaultIfEmpty(String.format("%012d", 1)).block();
	}

	private String getName(List<String> holderDoc) {
		String documentNumber = "";
		String name = "";

		if (!holderDoc.isEmpty()) {
			documentNumber = holderDoc.get(0);
		}

		CustomerEntity customer = customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber).toFuture()
				.join();

		if (customer != null) {
			if (customer.getPersonType().equalsIgnoreCase(Constants.PERSON_TYPE_PERSONAL)) {
				return customer.getFullName();
			} else if (customer.getPersonType().equalsIgnoreCase(Constants.PERSON_TYPE_BUSINESS)) {
				return customer.getCompanyName();
			}
		}
		return name;
	}

	private Double getCommission(String primaryAccount, String transactionType) {
		Double commission = 0.00;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
		LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
				.with(LocalTime.MAX);

		List<TransactionEntity> transactions = transactionRepository.findByAccountNumberAndIsActiveTrue(primaryAccount)
				.toStream()
				.filter(transaction -> transaction.getTransactionType().equals(transactionType)
						&& !transaction.getCreateDate().isBefore(startOfMonth)
						&& !transaction.getCreateDate().isAfter(endOfMonth))
				.collect(Collectors.toList());

		boolean isExceeded = transactions.size() > 10;

		if (isExceeded) {
			commission = 1.99;
		}

		return commission;
	}

	private String getPrimaryAccount(DepositRequest depositRequest) {
		String primaryAccount = null;

		if (depositRequest.getDebitCardNumber() != null) {
			DebitCardEntity debitCardEntity = debitCardRepository
					.findByDebitCardNumberAndIsActiveTrue(depositRequest.getDebitCardNumber()).block();

			if (debitCardEntity == null) {
				throw new IllegalArgumentException("card does not exist");
			}

			primaryAccount = debitCardEntity.getPrimaryAccount();
		}

		return primaryAccount;
	}

}
