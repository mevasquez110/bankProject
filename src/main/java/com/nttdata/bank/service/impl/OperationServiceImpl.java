package com.nttdata.bank.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.Consumption;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.entity.YankiEntity;
import com.nttdata.bank.mapper.TransactionMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditCardScheduleRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CreditScheduleRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.repository.TransactionRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.ProductResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.util.Constants;

/**
 * TransactionServiceImpl is the implementation class for the TransactionService
 * interface. This class provides the actual logic for handling
 * transaction-related operations such as making deposits, making withdrawals,
 * paying installments, checking transactions, and charging consumption.
 */
@Service
public class OperationServiceImpl implements OperationService {

	@Autowired
	private DebitCardRepository debitCardRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountsService accountService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private YankiRepository yankiRepository;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private CreditService creditService;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private CreditScheduleRepository creditScheduleRepository;

	@Autowired
	private CreditCardScheduleRepository creditCardScheduleRepository;

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
		String accountReceive = getPrimaryAccount(depositRequest);
		String transactionType = Constants.TRANSACTION_TYPE_DEPOSIT;
		Double commission = -getCommission(accountReceive);

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType,
						depositRequest.getAmount() + commission, null, generateUniqueOperationNumber(), null,
						accountReceive, null, null, getName(depositRequest.getDocumentNumber())))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberReceive(), transactionEntity.getAmount());
		return TransactionMapper.mapperToResponse(transactionEntity);
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
		DebitCardEntity debitCardEntity = Optional
				.ofNullable(debitCardRepository
						.findByDebitCardNumberAndIsActiveTrue(withdrawalRequest.getDebitCardNumber()).block())
				.orElseThrow(() -> new IllegalArgumentException("card does not exist"));

		String transactionType = Constants.TRANSACTION_TYPE_WITHDRAWAL;
		Double commission = 0.00;
		Double balance = 0.00;
		String accountWithdraws = debitCardEntity.getPrimaryAccount();
		Double amount = -withdrawalRequest.getAmount();
		Boolean hasBalance = false;
		int i = 0;

		do {
			if (i > 0) {
				accountWithdraws = debitCardEntity.getAssociatedAccounts().get(i);
			}

			commission = -getCommission(accountWithdraws);
			balance = -(amount + commission);

			hasBalance = accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(accountWithdraws, balance)
					.block();

			i++;
		} while (!hasBalance && debitCardEntity.getAssociatedAccounts().size() > i);

		if (!hasBalance) {
			throw new IllegalArgumentException(
					"None of the associated accounts have enough balance to make the withdrawal.");
		}

		String operationNumber = generateUniqueOperationNumber();

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(LocalDateTime.now(), commission, transactionType, amount, null,
						operationNumber, null, accountWithdraws, null, getName(withdrawalRequest.getDocumentNumber()),
						null))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberWithdraws(), transactionEntity.getAmount());
		return TransactionMapper.mapperToResponse(transactionEntity);
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
		String accountNumberWithdraws = accountTransferRequest.getAccountNumberWithdraws();
		String accountNumberReceive = accountTransferRequest.getAccountNumberReceive();

		if (accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(accountNumberWithdraws, amount).block()) {
			throw new IllegalArgumentException("The account does not have sufficient balance for the transfer.");
		}

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType, amount,
						accountNumberReceive, generateUniqueOperationNumber(), null, accountNumberWithdraws, null,
						getName(accountTransferRequest.getDocumentNumberWithdraws()),
						getName(accountTransferRequest.getDocumentNumberReceive())))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberWithdraws(), -transactionEntity.getAmount());
		updateAccount(transactionEntity.getAccountNumberReceive(), transactionEntity.getAmount());
		return TransactionMapper.mapperToResponse(transactionEntity);
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
		YankiEntity yankiWithdraws = Optional.ofNullable(yankiRepository
				.findByPhoneNumberAndIsActiveTrue(mobileTransferRequest.getMobileNumberWithdraws()).toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException("yanki Withdraws does not exist"));

		YankiEntity yankiReceive = Optional
				.ofNullable(
						yankiRepository.findByPhoneNumberAndIsActiveTrue(mobileTransferRequest.getMobileNumberReceive())
								.toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException("yanki Receive does not exist"));

		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberWithdraws(yankiWithdraws.getAccountNumber());
		accountTransferRequest.setAccountNumberReceive(yankiReceive.getAccountNumber());
		accountTransferRequest.setAccountNumberWithdraws(mobileTransferRequest.getDocumentNumberWithdraws());
		accountTransferRequest.setAccountNumberReceive(mobileTransferRequest.getDocumentNumberReceive());
		accountTransferRequest.setAmount(mobileTransferRequest.getAmount());
		return makeAccountTransfer(accountTransferRequest);
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
		LocalDateTime transactionDate = LocalDateTime.now();

		if (creditRepository.existsByIdAndIsActiveTrue(payCreditRequest.getCreditId()).block()) {
			throw new IllegalArgumentException("credit does not exist or does not active");
		}

		if (accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(payCreditRequest.getAccountNumber(),
				payCreditRequest.getAmount()).block()) {
			throw new IllegalArgumentException("The account has no balance for this transaction");
		}

		updateAccount(payCreditRequest.getAccountNumber(), -payCreditRequest.getAmount());

		List<CreditScheduleEntity> listSchedule = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(payCreditRequest.getCreditId(), transactionDate)
				.collectList().block();

		Double share = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(payCreditRequest.getCreditId(), transactionDate)
				.collectList().block().stream().mapToDouble(CreditScheduleEntity::getCurrentDebt).sum();

		List<CreditScheduleEntity> paymentSchedule = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateAfter(payCreditRequest.getCreditId(), transactionDate)
				.collectList().block();

		Double totalDebt = paymentSchedule.stream().max(Comparator.comparingDouble(CreditScheduleEntity::getTotalDebt))
				.map(CreditScheduleEntity::getTotalDebt).get() + share;

		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionDate, 0.00,
				Constants.TRANSACTION_TYPE_PAY_CREDIT, payCreditRequest.getAmount(), null,
				generateUniqueOperationNumber(), payCreditRequest.getCreditId(), payCreditRequest.getAccountNumber(),
				null, getName(payCreditRequest.getDocumentNumber()), null);

		transactionEntity = transactionRepository.save(transactionEntity).block();
		payCreditDebt(payCreditRequest, listSchedule, share, paymentSchedule, totalDebt);
		creditService.desactivateCredit(payCreditRequest.getCreditId());
		return TransactionMapper.mapperToResponse(transactionEntity);
	}

	/**
	 * Retrieves a list of products associated with the given document number.
	 *
	 * @param documentNumber the document number to search for products.
	 * @return a list of ProductResponse objects, representing the products found.
	 * @throws IllegalArgumentException if the document number is null or empty.
	 */
	@Override
	public List<ProductResponse> getProducts(String documentNumber) {
		List<ProductResponse> products = new ArrayList<>();

		Optional.ofNullable(
				creditRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
				.ifPresent(credits -> credits.forEach(creditEntity -> {
					ProductResponse product = new ProductResponse();
					product.setCreditId(creditEntity.getId());
					product.setProductType(Constants.PRODUCT_CREDIT);
					products.add(product);
				}));

		Optional.ofNullable(
				creditCardRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber).collectList().block())
				.ifPresent(creditCards -> creditCards.forEach(creditCardEntity -> {
					ProductResponse product = new ProductResponse();
					product.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
					product.setProductType(Constants.PRODUCT_CREDIT_CARD);
					products.add(product);
				}));

		Optional.ofNullable(
				accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber).collectList().block())
				.ifPresent(accounts -> accounts.forEach(accountEntity -> {
					ProductResponse product = new ProductResponse();
					product.setAccountNumber(accountEntity.getAccountNumber());
					product.setProductType(accountEntity.getAccountType());
					products.add(product);
				}));

		return products;
	}

	/**
	 * Check and retrieve a list of recent transactions for a given document number.
	 *
	 * @param documentNumber the document number to search for transactions
	 * @return a list of TransactionResponse objects, representing the recent
	 *         transactions associated with the provided document number
	 */
	@Override
	public List<TransactionResponse> checkTransactions(String documentNumber) {
		return accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber).collectList().block().stream()
				.flatMap(
						account -> transactionRepository
								.findAllByAccountNumberWithdrawsOrAccountNumberReceiveAndIsActiveTrue(
										account.getAccountNumber(), account.getAccountNumber())
								.collectList().block().stream())
				.sorted(Comparator.comparing(TransactionEntity::getCreateDate).reversed()).limit(10)
				.map(TransactionMapper::mapperToResponse).collect(Collectors.toList());
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
		TransactionResponse transactionResponse = new TransactionResponse();
		LocalDateTime transactionDate = LocalDateTime.now();

		if (creditCardRepository.existsByCreditCardNumberAndIsActiveTrue(payCreditCardRequest.getCreditCardNumber())
				.block()) {
			throw new IllegalArgumentException("credit card does not exist or does not active");
		}

		if (accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(payCreditCardRequest.getAccountNumber(),
				payCreditCardRequest.getAmount()).block()) {
			throw new IllegalArgumentException("The account has no balance for this transaction");
		}

		List<CreditCardScheduleEntity> listSchedule = creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
						payCreditCardRequest.getCreditCardNumber(), LocalDate.now())
				.collectList().block();

		Double share = listSchedule.stream().mapToDouble(CreditCardScheduleEntity::getCurrentDebt).sum();

		List<CreditCardScheduleEntity> paymentSchedule = creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateAfter(payCreditCardRequest.getCreditCardNumber(),
						LocalDateTime.now())
				.collectList().block();

		Double totalDebt = paymentSchedule.stream().flatMap(schedule -> schedule.getConsumptionQuota().stream())
				.mapToDouble(Consumption::getAmount).sum() + share;

		if (payCreditCardRequest.getAccountNumber() != null) {
			if (payCreditCardRequest.getDocumentNumber() == null) {
				throw new IllegalArgumentException("The document number is null");
			}

			updateAccount(payCreditCardRequest.getAccountNumber(), -payCreditCardRequest.getAmount());

			TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionDate, 0.00,
					Constants.TRANSACTION_TYPE_PAY_CREDIT_CARD, payCreditCardRequest.getAmount(), null,
					generateUniqueOperationNumber(), null, payCreditCardRequest.getAccountNumber(),
					payCreditCardRequest.getCreditCardNumber(), getName(payCreditCardRequest.getDocumentNumber()),
					null);

			transactionEntity = transactionRepository.save(transactionEntity).block();
			transactionResponse = TransactionMapper.mapperToResponse(transactionEntity);
		}

		payCreditCardDebt(payCreditCardRequest, listSchedule, share, paymentSchedule, totalDebt);
		return transactionResponse;
	}

	/**
	 * Generates a unique operation number for a transaction.
	 *
	 * @return a unique operation number in the format of a 12-digit string
	 */
	private String generateUniqueOperationNumber() {
		return transactionRepository.findFirstByOrderByOperationNumberDesc().map(transaction -> {
			int newOperationNumber = Integer.parseInt(transaction.getOperationNumber()) + 1;
			return String.format("%012d", newOperationNumber);
		}).defaultIfEmpty(String.format("%012d", 1)).block();
	}

	/**
	 * Retrieves the name of a customer based on the provided document number.
	 *
	 * @param documentNumber the document number to search for the customer
	 * @return the full name of the customer if the person type is personal, or the
	 *         company name otherwise
	 * @throws IllegalArgumentException if the customer does not exist
	 */
	private String getName(String documentNumber) {
		CustomerEntity customer = customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber).toFuture()
				.join();

		if (customer != null) {
			if (customer.getPersonType().equalsIgnoreCase(Constants.PERSON_TYPE_PERSONAL)) {
				return customer.getFullName();
			} else {
				return customer.getCompanyName();
			}
		} else {
			throw new IllegalArgumentException("customer does not exist");
		}
	}

	/**
	 * Calculates the commission for a given account and transaction type.
	 *
	 * @param accountNumber the account number to check for transactions
	 * @return the commission amount, 1.99 if the number of transactions of the
	 *         given type in the current month exceeds 10, otherwise 0.00
	 */
	private Double getCommission(String accountNumber) {
		Double commission = 0.00;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN);
		LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
				.with(LocalTime.MAX);

		List<TransactionEntity> transactions = transactionRepository
				.findAllByAccountNumberWithdrawsOrAccountNumberReceiveAndIsActiveTrue(accountNumber, accountNumber)
				.toStream()
				.filter(transaction -> (transaction.getTransactionType().equals(Constants.TRANSACTION_TYPE_WITHDRAWAL)
						|| transaction.getTransactionType().equals(Constants.TRANSACTION_TYPE_DEPOSIT))
						&& !transaction.getCreateDate().isBefore(startOfMonth)
						&& !transaction.getCreateDate().isAfter(endOfMonth))
				.collect(Collectors.toList());

		boolean isExceeded = transactions.size() > 10;

		if (isExceeded) {
			commission = 1.99;
		}

		return commission;
	}

	/**
	 * Retrieves the primary account associated with the given deposit request.
	 *
	 * @param depositRequest the deposit request containing the debit card number
	 * @return the primary account number if the debit card number is valid and
	 *         active, otherwise null
	 * @throws IllegalArgumentException if the debit card does not exist
	 */
	private String getPrimaryAccount(DepositRequest depositRequest) {
		return Optional.ofNullable(depositRequest.getDebitCardNumber()).map(debitCardNumber -> {
			DebitCardEntity debitCardEntity = Optional
					.ofNullable(debitCardRepository.findByDebitCardNumberAndIsActiveTrue(debitCardNumber).block())
					.orElseThrow(() -> new IllegalArgumentException("card does not exist"));
			return debitCardEntity.getPrimaryAccount();
		}).orElse(null);
	}

	/**
	 * Processes the payment of debt based on the provided pay credit request and
	 * schedules.
	 *
	 * @param payCreditRequest the request containing the payment details
	 * @param listSchedule     the current list of credit schedules to be paid
	 * @param share            the share amount for current installments
	 * @param paymentSchedule  the list of credit schedules for the total debt
	 *                         payment
	 * @param totalDebt        the total amount of debt to be paid
	 * @throws IllegalArgumentException if the payment amount exceeds the total debt
	 */
	private void payCreditDebt(PayCreditRequest payCreditRequest, List<CreditScheduleEntity> listSchedule, Double share,
			List<CreditScheduleEntity> paymentSchedule, Double totalDebt) {
		if (totalDebt > payCreditRequest.getAmount()) {
			throw new IllegalArgumentException("the amount exceeds the total debt");
		} else if (totalDebt == payCreditRequest.getAmount()) {
			payCreditCurrentInstallments(listSchedule, share);

			if (totalDebt > 0) {
				payCreditTotalDebt(paymentSchedule, totalDebt);
			}
		} else if (totalDebt < payCreditRequest.getAmount()) {
			payCreditCurrentInstallments(listSchedule, payCreditRequest.getAmount());
		}
	}

	/**
	 * Processes the payment of debt based on the provided pay credit card request
	 * and schedules.
	 *
	 * @param payCreditCardRequest the request containing the payment details
	 * @param listSchedule         the current list of credit schedules to be paid
	 * @param share                the share amount for current installments
	 * @param paymentSchedule      the list of credit schedules for the total debt
	 *                             payment
	 * @param totalDebt            the total amount of debt to be paid
	 * @throws IllegalArgumentException if the payment amount exceeds the total debt
	 */
	private void payCreditCardDebt(PayCreditCardRequest payCreditCardRequest,
			List<CreditCardScheduleEntity> listSchedule, Double share, List<CreditCardScheduleEntity> paymentSchedule,
			Double totalDebt) {
		if (totalDebt > payCreditCardRequest.getAmount()) {
			throw new IllegalArgumentException("the amount exceeds the total debt");
		} else if (totalDebt == payCreditCardRequest.getAmount()) {
			payCreditCardCurrentInstallments(listSchedule, share);

			if (totalDebt > 0) {
				payCreditCardTotalDebt(paymentSchedule, totalDebt);
			}
		} else if (totalDebt < payCreditCardRequest.getAmount()) {
			payCreditCardCurrentInstallments(listSchedule, payCreditCardRequest.getAmount());
		}
	}

	/**
	 * Marks the entire debt as paid for the provided payment schedule.
	 *
	 * @param paymentSchedule the list of credit schedules to mark as paid
	 * @param totalDebt       the total amount of debt to be marked as paid
	 */
	private void payCreditTotalDebt(List<CreditScheduleEntity> paymentSchedule, Double totalDebt) {
		for (CreditScheduleEntity creditScheduleEntity : paymentSchedule) {
			paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, true);
		}
	}

	/**
	 * Marks the entire debt as paid for the provided payment schedule.
	 *
	 * @param paymentSchedule the list of credit card schedules to mark as paid
	 * @param totalDebt       the total amount of debt to be marked as paid
	 */
	private void payCreditCardTotalDebt(List<CreditCardScheduleEntity> paymentSchedule, Double totalDebt) {
		for (CreditCardScheduleEntity creditCardScheduleEntity : paymentSchedule) {
			paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, true);
		}
	}

	/**
	 * Processes the payment of current installments based on the provided amount.
	 *
	 * @param listSchedule the list of credit schedules to be paid
	 * @param amount       the amount available for payment
	 */
	private void payCreditCurrentInstallments(List<CreditScheduleEntity> listSchedule, Double amount) {
		for (CreditScheduleEntity creditScheduleEntity : listSchedule) {
			if (creditScheduleEntity.getCurrentDebt() > amount) {
				processCreditPayment(creditScheduleEntity, amount);
				continue;
			} else if (creditScheduleEntity.getCurrentDebt() < amount) {
				paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, true);
			} else if (creditScheduleEntity.getCurrentDebt() == amount) {
				paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, true);
				continue;
			}

			amount -= creditScheduleEntity.getCurrentDebt();
		}
	}

	/**
	 * Processes the payment of current installments based on the provided amount.
	 *
	 * @param listSchedule the list of credit card schedules to be paid
	 * @param amount       the amount available for payment
	 */
	private void payCreditCardCurrentInstallments(List<CreditCardScheduleEntity> listSchedule, Double amount) {
		for (CreditCardScheduleEntity creditCardScheduleEntity : listSchedule) {
			if (creditCardScheduleEntity.getCurrentDebt() > amount) {
				processCreditCardPayment(creditCardScheduleEntity, amount);
				continue;
			} else if (creditCardScheduleEntity.getCurrentDebt() < amount) {
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, true);
			} else if (creditCardScheduleEntity.getCurrentDebt() == amount) {
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, true);
				continue;
			}

			amount -= creditCardScheduleEntity.getCurrentDebt();
		}
	}

	/**
	 * Processes the payment for a given credit schedule entity based on the
	 * provided amount.
	 *
	 * @param creditScheduleEntity the credit schedule entity to process the payment
	 *                             for
	 * @param amount               the amount available for payment
	 */
	private void processCreditPayment(CreditScheduleEntity creditScheduleEntity, Double amount) {
		while (true) {
			if (creditScheduleEntity.getPrincipalAmount() > amount) {
				Double principalAmount = creditScheduleEntity.getPrincipalAmount() - amount;
				paidCreditDebt(creditScheduleEntity, principalAmount, null, null, null);
				continue;
			} else if (creditScheduleEntity.getPrincipalAmount() < amount) {
				processCreditInterestAndLateAmount(creditScheduleEntity, amount);
			} else if (creditScheduleEntity.getPrincipalAmount() == amount) {
				paidCreditDebt(creditScheduleEntity, 0.00, null, null, null);
				continue;
			}
		}
	}

	/**
	 * Processes the payment for a given credit card schedule entity based on the
	 * provided amount.
	 *
	 * @param creditCardScheduleEntity the credit card schedule entity to process
	 *                                 the payment for
	 * @param amount                   the amount available for payment
	 */
	private void processCreditCardPayment(CreditCardScheduleEntity creditCardScheduleEntity, Double amount) {
		while (true) {
			if (creditCardScheduleEntity.getPrincipalAmount() > amount) {
				Double principalAmount = creditCardScheduleEntity.getPrincipalAmount() - amount;
				paidCreditCardDebt(creditCardScheduleEntity, principalAmount, null, null, null);
				continue;
			} else if (creditCardScheduleEntity.getPrincipalAmount() < amount) {
				processCreditCardInterestAndLateAmount(creditCardScheduleEntity, amount);
			} else if (creditCardScheduleEntity.getPrincipalAmount() == amount) {
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, null, null, null);
				continue;
			}
		}
	}

	/**
	 * Processes the interest and late amount for a given credit schedule entity
	 * based on the provided amount.
	 *
	 * @param creditScheduleEntity the credit schedule entity to process the
	 *                             interest and late amount for
	 * @param amount               the amount available for payment
	 */
	private void processCreditInterestAndLateAmount(CreditScheduleEntity creditScheduleEntity, Double amount) {
		amount -= creditScheduleEntity.getPrincipalAmount();

		if (creditScheduleEntity.getInterestAmount() > amount) {
			Double interestAmount = creditScheduleEntity.getInterestAmount() - amount;
			paidCreditDebt(creditScheduleEntity, 0.00, interestAmount, null, null);
		} else if (creditScheduleEntity.getInterestAmount() < amount) {
			amount -= creditScheduleEntity.getInterestAmount();
			processCreditLateAmount(creditScheduleEntity, amount);
		} else if (creditScheduleEntity.getInterestAmount() == amount) {
			processCreditLateAmount(creditScheduleEntity, amount);
		}
	}

	/**
	 * Processes the interest and late amount for a given credit card schedule
	 * entity based on the provided amount.
	 *
	 * @param creditCardScheduleEntity the credit card schedule entity to process
	 *                                 the interest and late amount for
	 * @param amount                   the amount available for payment
	 */
	private void processCreditCardInterestAndLateAmount(CreditCardScheduleEntity creditCardScheduleEntity,
			Double amount) {
		amount -= creditCardScheduleEntity.getPrincipalAmount();

		if (creditCardScheduleEntity.getInterestAmount() > amount) {
			Double interestAmount = creditCardScheduleEntity.getInterestAmount() - amount;
			paidCreditCardDebt(creditCardScheduleEntity, 0.00, interestAmount, null, null);
		} else if (creditCardScheduleEntity.getInterestAmount() < amount) {
			amount -= creditCardScheduleEntity.getInterestAmount();
			processCrediCardLateAmount(creditCardScheduleEntity, amount);
		} else if (creditCardScheduleEntity.getInterestAmount() == amount) {
			processCrediCardLateAmount(creditCardScheduleEntity, amount);
		}
	}

	/**
	 * Processes the late amount for a given credit schedule entity based on the
	 * provided amount.
	 *
	 * @param creditScheduleEntity the credit schedule entity to process the late
	 *                             amount for
	 * @param amount               the amount available for payment
	 */
	private void processCreditLateAmount(CreditScheduleEntity creditScheduleEntity, double amount) {
		if (creditScheduleEntity.getLateAmount() > 0) {
			if (creditScheduleEntity.getLateAmount() > amount) {
				Double lateAmount = creditScheduleEntity.getLateAmount() - amount;
				paidCreditDebt(creditScheduleEntity, 0.00, 0.00, lateAmount, null);
			} else if (creditScheduleEntity.getLateAmount() < amount) {
				paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, true);
			} else if (creditScheduleEntity.getLateAmount() == amount) {
				paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, true);
			}
		} else {
			paidCreditDebt(creditScheduleEntity, 0.00, 0.00, 0.00, null);
		}
	}

	/**
	 * Processes the late amount for a given credit card schedule entity based on
	 * the provided amount.
	 *
	 * @param creditCardScheduleEntity the credit card schedule entity to process
	 *                                 the late amount for
	 * @param amount                   the amount available for payment
	 */
	private void processCrediCardLateAmount(CreditCardScheduleEntity creditCardScheduleEntity, Double amount) {
		if (creditCardScheduleEntity.getLateAmount() > 0) {
			if (creditCardScheduleEntity.getLateAmount() > amount) {
				Double lateAmount = creditCardScheduleEntity.getLateAmount() - amount;
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, lateAmount, null);
			} else if (creditCardScheduleEntity.getLateAmount() < amount) {
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, true);
			} else if (creditCardScheduleEntity.getLateAmount() == amount) {
				paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, true);
			}
		} else {
			paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, null);
		}
	}

	/**
	 * Updates the credit schedule entity with the provided amounts and payment
	 * status.
	 *
	 * @param creditScheduleEntity the credit schedule entity to be updated
	 * @param principalAmount      the new principal amount, or the current
	 *                             principal amount if null
	 * @param interestAmount       the new interest amount, or the current interest
	 *                             amount if null
	 * @param lateAmount           the new late amount, or the current late amount
	 *                             if null
	 * @param paid                 the new payment status, or the current payment
	 *                             status if null
	 */
	private void paidCreditDebt(CreditScheduleEntity creditScheduleEntity, Double principalAmount,
			Double interestAmount, Double lateAmount, Boolean paid) {
		creditScheduleEntity.setPaid(paid != null ? paid : creditScheduleEntity.getPaid());
		creditScheduleEntity.setLateAmount(lateAmount != null ? lateAmount : creditScheduleEntity.getLateAmount());

		creditScheduleEntity
				.setInterestAmount(interestAmount != null ? interestAmount : creditScheduleEntity.getInterestAmount());

		creditScheduleEntity.setPrincipalAmount(
				principalAmount != null ? principalAmount : creditScheduleEntity.getPrincipalAmount());

		creditScheduleRepository.save(creditScheduleEntity);
	}

	/**
	 * Updates the credit card schedule entity with the provided amounts and payment
	 * status.
	 *
	 * @param creditCardScheduleEntity the credit card schedule entity to be updated
	 * @param principalAmount          the new principal amount, or the current
	 *                                 principal amount if null
	 * @param interestAmount           the new interest amount, or the current
	 *                                 interest amount if null
	 * @param lateAmount               the new late amount, or the current late
	 *                                 amount if null
	 * @param paid                     the new payment status, or the current
	 *                                 payment status if null
	 */
	private void paidCreditCardDebt(CreditCardScheduleEntity creditCardScheduleEntity, Double principalAmount,
			Double interestAmount, Double lateAmount, Boolean paid) {
		creditCardScheduleEntity.setPaid(paid != null ? paid : creditCardScheduleEntity.getPaid());

		creditCardScheduleEntity
				.setLateAmount(lateAmount != null ? lateAmount : creditCardScheduleEntity.getLateAmount());

		creditCardScheduleEntity.setInterestAmount(
				interestAmount != null ? interestAmount : creditCardScheduleEntity.getInterestAmount());

		creditCardScheduleEntity.setPrincipalAmount(
				principalAmount != null ? principalAmount : creditCardScheduleEntity.getPrincipalAmount());

		creditCardScheduleRepository.save(creditCardScheduleEntity);
	}

	/**
	 * Updates the account with the specified account number and amount.
	 *
	 * @param accountNumber the account number to be updated
	 * @param amount        the amount to be updated in the account
	 */
	private void updateAccount(String accountNumber, Double amount) {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber(accountNumber);
		updateAccountRequest.setAmount(amount);
		accountService.updateAccount(updateAccountRequest);
	}

}
