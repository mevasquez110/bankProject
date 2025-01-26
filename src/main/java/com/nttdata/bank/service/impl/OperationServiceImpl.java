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
import com.nttdata.bank.entity.CreditCardEntity;
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
import reactor.core.publisher.Mono;

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
	public Mono<TransactionResponse> makeDeposit(DepositRequest depositRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();
		String accountReceive = getPrimaryAccount(depositRequest);
		String transactionType = Constants.TRANSACTION_TYPE_DEPOSIT;
		Double commission = -getCommission(accountReceive);

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType,
						depositRequest.getAmount() + commission, null,
						generateUniqueOperationNumber(), null,
						accountReceive, null, null, getName(depositRequest.getDocumentNumber())))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberReceive(), transactionEntity.getAmount());
		return Mono.just(TransactionMapper.mapperToResponse(transactionEntity));
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
	public Mono<TransactionResponse> makeWithdrawal(WithdrawalRequest withdrawalRequest) {
		DebitCardEntity debitCardEntity = Optional
				.ofNullable(debitCardRepository
						.findByDebitCardNumberAndIsActiveTrue(
								withdrawalRequest.getDebitCardNumber())
						.toFuture().join())
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

			hasBalance = accountRepository
					.existsByAccountNumberAndAmountGreaterThanEqual(accountWithdraws, balance)
					.toFuture().join();

			i++;
		} while (!hasBalance && debitCardEntity.getAssociatedAccounts().size() > i);

		if (!hasBalance) {
			throw new IllegalArgumentException(
					"None of the associated accounts have enough balance to make the withdrawal.");
		}

		String operationNumber = generateUniqueOperationNumber();

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(LocalDateTime.now(), commission,
						transactionType, amount, null,
						operationNumber, null, accountWithdraws, null,
						getName(withdrawalRequest.getDocumentNumber()),
						null))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberWithdraws(), transactionEntity.getAmount());
		return Mono.just(TransactionMapper.mapperToResponse(transactionEntity));
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
	public Mono<TransactionResponse> makeAccountTransfer(
			AccountTransferRequest accountTransferRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();
		Double commission = 0.00;
		String transactionType = Constants.TRANSACTION_TYPE_BANK_TRANSFER;
		Double amount = accountTransferRequest.getAmount();
		String accountNumberWithdraws = accountTransferRequest.getAccountNumberWithdraws();
		String accountNumberReceive = accountTransferRequest.getAccountNumberReceive();

		if (accountRepository
				.existsByAccountNumberAndAmountGreaterThanEqual(accountNumberWithdraws, amount)
				.toFuture().join()) {
			throw new IllegalArgumentException(
					"The account does not have sufficient balance for the transfer.");
		}

		TransactionEntity transactionEntity = transactionRepository
				.save(TransactionMapper.mapperToEntity(transactionDate, commission, transactionType,
						amount,
						accountNumberReceive, generateUniqueOperationNumber(), null,
						accountNumberWithdraws, null,
						getName(accountTransferRequest.getDocumentNumberWithdraws()),
						getName(accountTransferRequest.getDocumentNumberReceive())))
				.toFuture().join();

		updateAccount(transactionEntity.getAccountNumberWithdraws(),
				-transactionEntity.getAmount());
		updateAccount(transactionEntity.getAccountNumberReceive(), transactionEntity.getAmount());
		return Mono.just(TransactionMapper.mapperToResponse(transactionEntity));
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
	public Mono<TransactionResponse> makeMobileTransfer(
			MobileTransferRequest mobileTransferRequest) {
		YankiEntity yankiWithdraws = Optional.ofNullable(yankiRepository
				.findByPhoneNumberAndIsActiveTrue(mobileTransferRequest.getMobileNumberWithdraws())
				.toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException("yanki Withdraws does not exist"));

		YankiEntity yankiReceive = Optional
				.ofNullable(
						yankiRepository
								.findByPhoneNumberAndIsActiveTrue(
										mobileTransferRequest.getMobileNumberReceive())
								.toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException("yanki Receive does not exist"));

		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberWithdraws(yankiWithdraws.getAccountNumber());
		accountTransferRequest.setAccountNumberReceive(yankiReceive.getAccountNumber());
		accountTransferRequest
				.setAccountNumberWithdraws(mobileTransferRequest.getDocumentNumberWithdraws());
		accountTransferRequest
				.setAccountNumberReceive(mobileTransferRequest.getDocumentNumberReceive());
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
	public Mono<TransactionResponse> payCredit(PayCreditRequest payCreditRequest) {
		LocalDateTime transactionDate = LocalDateTime.now();

		if (creditRepository.existsByIdAndIsActiveTrue(payCreditRequest.getCreditId()).toFuture()
				.join()) {
			throw new IllegalArgumentException("credit does not exist or does not active");
		}

		if (accountRepository
				.existsByAccountNumberAndAmountGreaterThanEqual(payCreditRequest.getAccountNumber(),
						payCreditRequest.getAmount())
				.toFuture().join()) {
			throw new IllegalArgumentException("The account has no balance for this transaction");
		}

		updateAccount(payCreditRequest.getAccountNumber(), -payCreditRequest.getAmount());

		List<CreditScheduleEntity> overduePaymentSchedule = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
						payCreditRequest.getCreditId(), transactionDate)
				.collectList().toFuture().join().stream()
				.sorted(Comparator.comparing(CreditScheduleEntity::getPaymentDate))
				.collect(Collectors.toList());

		Double share = overduePaymentSchedule.stream()
				.mapToDouble(CreditScheduleEntity::getCurrentDebt).sum();

		List<CreditScheduleEntity> upcomingPaymentSchedule = creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateAfter(payCreditRequest.getCreditId(),
						transactionDate)
				.collectList().toFuture().join().stream()
				.sorted(Comparator.comparing(CreditScheduleEntity::getPaymentDate))
				.collect(Collectors.toList());

		Double totalDebt = upcomingPaymentSchedule.stream()
				.mapToDouble(CreditScheduleEntity::getCurrentDebt).sum()
				+ share;

		TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionDate,
				0.00,
				Constants.TRANSACTION_TYPE_PAY_CREDIT, payCreditRequest.getAmount(), null,
				generateUniqueOperationNumber(), payCreditRequest.getCreditId(),
				payCreditRequest.getAccountNumber(),
				null, getName(payCreditRequest.getDocumentNumber()), null);

		transactionEntity = transactionRepository.save(transactionEntity).toFuture().join();
		payCreditDebt(payCreditRequest.getAmount(), overduePaymentSchedule, share,
				upcomingPaymentSchedule, totalDebt);

		if (!creditScheduleRepository.existsByIdAndPaidFalse(payCreditRequest.getCreditId())
				.toFuture().join()) {
			creditService.desactivateCredit(payCreditRequest.getCreditId());
		}

		return Mono.just(TransactionMapper.mapperToResponse(transactionEntity));
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
				creditRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber)
						.collectList().block())
				.ifPresent(credits -> credits.forEach(creditEntity -> {
					ProductResponse product = new ProductResponse();
					product.setCreditId(creditEntity.getId());
					product.setProductType(Constants.PRODUCT_CREDIT);
					products.add(product);
				}));

		Optional.ofNullable(
				creditCardRepository.findAllByDocumentNumberAndIsActiveTrue(documentNumber)
						.collectList().block())
				.ifPresent(creditCards -> creditCards.forEach(creditCardEntity -> {
					ProductResponse product = new ProductResponse();
					product.setCreditCardNumber(creditCardEntity.getCreditCardNumber());
					product.setProductType(Constants.PRODUCT_CREDIT_CARD);
					products.add(product);
				}));

		Optional.ofNullable(
				accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber)
						.collectList().block())
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
		return accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber)
				.collectList().block().stream()
				.flatMap(account -> transactionRepository.findAllByIsActiveTrue().collectList()
						.block().stream()
						.filter(transaction -> transaction.getAccountNumberReceive()
								.equalsIgnoreCase(account.getAccountNumber())
								|| transaction.getAccountNumberWithdraws()
										.equalsIgnoreCase(account.getAccountNumber())))
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
	public Mono<TransactionResponse> payCreditCard(PayCreditCardRequest payCreditCardRequest) {
		TransactionResponse transactionResponse = new TransactionResponse();
		LocalDateTime transactionDate = LocalDateTime.now();

		if (creditCardRepository
				.existsByCreditCardNumberAndIsActiveTrue(payCreditCardRequest.getCreditCardNumber())
				.toFuture().join()) {
			throw new IllegalArgumentException("credit card does not exist or does not active");
		}

		if (accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(
				payCreditCardRequest.getAccountNumber(),
				payCreditCardRequest.getAmount()).toFuture().join()) {
			throw new IllegalArgumentException("The account has no balance for this transaction");
		}

		List<CreditCardScheduleEntity> overduePaymentSchedule = creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
						payCreditCardRequest.getCreditCardNumber(), LocalDate.now())
				.collectList().toFuture().join();

		Double share = overduePaymentSchedule.stream()
				.mapToDouble(CreditCardScheduleEntity::getCurrentDebt).sum();

		List<CreditCardScheduleEntity> upcomingPaymentSchedule = creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateAfter(
						payCreditCardRequest.getCreditCardNumber(),
						LocalDateTime.now())
				.collectList().toFuture().join();

		Double totalDebt = upcomingPaymentSchedule.stream()
				.mapToDouble(CreditCardScheduleEntity::getCurrentDebt).sum()
				+ share;

		if (payCreditCardRequest.getAccountNumber() != null) {
			if (payCreditCardRequest.getDocumentNumber() == null) {
				throw new IllegalArgumentException("The document number is null");
			}

			updateAccount(payCreditCardRequest.getAccountNumber(),
					-payCreditCardRequest.getAmount());

			TransactionEntity transactionEntity = TransactionMapper.mapperToEntity(transactionDate,
					0.00,
					Constants.TRANSACTION_TYPE_PAY_CREDIT_CARD, payCreditCardRequest.getAmount(),
					null,
					generateUniqueOperationNumber(), null, payCreditCardRequest.getAccountNumber(),
					payCreditCardRequest.getCreditCardNumber(),
					getName(payCreditCardRequest.getDocumentNumber()),
					null);

			transactionEntity = transactionRepository.save(transactionEntity).toFuture().join();
			transactionResponse = TransactionMapper.mapperToResponse(transactionEntity);
		}

		Double balanceReturned = payCreditCardDebt(payCreditCardRequest.getAmount(),
				overduePaymentSchedule, share,
				upcomingPaymentSchedule, totalDebt);

		CreditCardEntity entity = creditCardRepository
				.findByCreditCardNumberAndIsActiveTrue(payCreditCardRequest.getCreditCardNumber())
				.toFuture().join();

		entity.setAvailableCredit(entity.getAvailableCredit() + balanceReturned);
		creditCardRepository.save(entity);
		return Mono.just(transactionResponse);
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
		}).defaultIfEmpty(String.format("%012d", 1)).toFuture().join();
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
		CustomerEntity customer = customerRepository
				.findByDocumentNumberAndIsActiveTrue(documentNumber).toFuture()
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
		LocalDateTime endOfMonth = now
				.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
				.with(LocalTime.MAX);

		List<TransactionEntity> transactions = transactionRepository.findAllByIsActiveTrue()
				.toStream()
				.filter(transaction -> (transaction.getTransactionType()
						.equals(Constants.TRANSACTION_TYPE_WITHDRAWAL)
						|| transaction.getTransactionType()
								.equals(Constants.TRANSACTION_TYPE_DEPOSIT))
						&& !transaction.getCreateDate().isBefore(startOfMonth)
						&& !transaction.getCreateDate().isAfter(endOfMonth)
						&& (transaction.getAccountNumberReceive().equalsIgnoreCase(accountNumber)
								|| transaction.getAccountNumberWithdraws()
										.equalsIgnoreCase(accountNumber)))
				.collect(Collectors.toList());

		boolean isExceeded = transactions.size() > 10;

		if (isExceeded) {
			commission = Constants.COMMISSION_ADD;
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
					.ofNullable(debitCardRepository
							.findByDebitCardNumberAndIsActiveTrue(debitCardNumber).toFuture()
							.join())
					.orElseThrow(() -> new IllegalArgumentException("card does not exist"));
			return debitCardEntity.getPrimaryAccount();
		}).orElse(null);
	}

	/**
	 * Processes the payment of debt based on the provided pay credit request and
	 * schedules.
	 *
	 * @param amount                  amount paid
	 * @param overduePaymentSchedule  the current list of credit schedules to be
	 *                                paid
	 * @param share                   the share amount for current installments
	 * @param upcomingPaymentSchedule the list of credit schedules for the total
	 *                                debt payment
	 * @param totalDebt               the total amount of debt to be paid
	 * @throws IllegalArgumentException if the payment amount exceeds the total debt
	 */
	private void payCreditDebt(Double amount, List<CreditScheduleEntity> overduePaymentSchedule,
			Double share,
			List<CreditScheduleEntity> upcomingPaymentSchedule, Double totalDebt) {
		List<CreditScheduleEntity> combinedSchedule = new ArrayList<>();
		combinedSchedule.addAll(overduePaymentSchedule);
		combinedSchedule.addAll(upcomingPaymentSchedule);

		if (totalDebt > amount) {
			throw new IllegalArgumentException("the amount exceeds the total debt");
		} else if (totalDebt == amount) {
			payCreditCurrentInstallments(combinedSchedule, totalDebt);
		} else if (totalDebt < amount) {
			if (share > amount) {
				payCreditCurrentInstallments(combinedSchedule, amount);
			} else if (share <= amount) {
				payCreditCurrentInstallments(overduePaymentSchedule, amount);
			}
		}
	}

	/**
	 * Processes the payment of debt based on the provided pay credit card request
	 * and schedules.
	 *
	 * @param amount                  amount paid
	 * @param overduePaymentSchedule  the current list of credit schedules to be
	 *                                paid
	 * @param share                   the share amount for current installments
	 * @param upcomingPaymentSchedule the list of credit schedules for the total
	 *                                payment
	 * @param totalDebt               the total amount of debt to be paid
	 * @return balance returned.
	 * @throws IllegalArgumentException if the payment amount exceeds the total debt
	 */
	private Double payCreditCardDebt(Double amount,
			List<CreditCardScheduleEntity> overduePaymentSchedule, Double share,
			List<CreditCardScheduleEntity> upcomingPaymentSchedule, Double totalDebt) {
		Double balanceReturned = 0.00;

		if (totalDebt > amount) {
			throw new IllegalArgumentException("the amount exceeds the total debt");
		} else if (totalDebt == amount) {
			balanceReturned += payCreditCardCurrentInstallments(overduePaymentSchedule, share);
			balanceReturned += totalDebt - share;
			payConsumptionCreditCard(upcomingPaymentSchedule, totalDebt - share);
		} else if (totalDebt < amount) {
			if (share > amount) {
				balanceReturned += payCreditCardCurrentInstallments(overduePaymentSchedule, amount);
				balanceReturned += totalDebt - amount;
				payConsumptionCreditCard(upcomingPaymentSchedule, totalDebt - amount);
			} else if (share <= amount) {
				balanceReturned += payCreditCardCurrentInstallments(overduePaymentSchedule, amount);
			}
		}
		return balanceReturned;
	}

	/**
	 * Method to pay off credit card consumption installments using an available
	 * amount. The method sorts consumptions by the number of installments in
	 * descending order and reduces the available amount from the consumptions,
	 * starting with those that have the highest number of installments, until the
	 * amount is exhausted.
	 *
	 * @param upcomingPaymentSchedule List of pending payment schedules for the
	 *                                credit card.
	 * @param amount                  Available amount to pay the consumptions.
	 */
	private void payConsumptionCreditCard(List<CreditCardScheduleEntity> upcomingPaymentSchedule,
			Double amount) {
		for (CreditCardScheduleEntity creditCardScheduleEntity : upcomingPaymentSchedule) {
			List<Consumption> consumptions = creditCardScheduleEntity.getConsumptionQuota();
			consumptions
					.sort(Comparator.comparing(Consumption::getNumberOfInstallments).reversed());

			for (Consumption consumption : consumptions) {
				if (amount <= 0) {
					break;
				}

				double remainingAmount = consumption.getAmount();

				if (remainingAmount <= amount) {
					amount -= remainingAmount;
					consumption.setAmount(0.0);
				} else {
					consumption.setAmount(remainingAmount - amount);
					amount = 0.0;
				}
			}
		}
	}

	/**
	 * Processes the payment of current installments based on the provided amount.
	 *
	 * @param listSchedule the list of credit schedules to be paid
	 * @param amount       the amount available for payment
	 */
	private void payCreditCurrentInstallments(List<CreditScheduleEntity> listSchedule,
			Double amount) {
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
	 * @return balance returned.
	 */
	private Double payCreditCardCurrentInstallments(List<CreditCardScheduleEntity> listSchedule,
			Double amount) {
		Double balanceReturned = 0.00;

		for (CreditCardScheduleEntity creditCardScheduleEntity : listSchedule) {
			if (creditCardScheduleEntity.getCurrentDebt() > amount) {
				balanceReturned += processCreditCardPayment(creditCardScheduleEntity, amount);
				continue;
			} else if (creditCardScheduleEntity.getCurrentDebt() < amount) {
				balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00,
						true);
			} else if (creditCardScheduleEntity.getCurrentDebt() == amount) {
				balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00,
						true);
				continue;
			}

			amount -= creditCardScheduleEntity.getCurrentDebt();
		}

		return balanceReturned;
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
	 * @return balance returned.
	 */
	private Double processCreditCardPayment(CreditCardScheduleEntity creditCardScheduleEntity,
			Double amount) {
		Double balanceReturned = 0.00;

		if (creditCardScheduleEntity.getPrincipalAmount() > amount) {
			Double principalAmount = creditCardScheduleEntity.getPrincipalAmount() - amount;
			balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, principalAmount, null,
					null, null);
		} else if (creditCardScheduleEntity.getPrincipalAmount() < amount) {
			balanceReturned += processCreditCardInterestAndLateAmount(creditCardScheduleEntity,
					amount);
		} else if (creditCardScheduleEntity.getPrincipalAmount() == amount) {
			balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, null, null, null);
		}

		return balanceReturned;
	}

	/**
	 * Processes the interest and late amount for a given credit schedule entity
	 * based on the provided amount.
	 *
	 * @param creditScheduleEntity the credit schedule entity to process the
	 *                             interest and late amount for
	 * @param amount               the amount available for payment
	 */
	private void processCreditInterestAndLateAmount(CreditScheduleEntity creditScheduleEntity,
			Double amount) {
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
	 * @return balance returned.
	 */
	private Double processCreditCardInterestAndLateAmount(
			CreditCardScheduleEntity creditCardScheduleEntity,
			Double amount) {
		Double balanceReturned = 0.00;

		amount -= creditCardScheduleEntity.getPrincipalAmount();

		if (creditCardScheduleEntity.getInterestAmount() > amount) {
			Double interestAmount = creditCardScheduleEntity.getInterestAmount() - amount;
			balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, interestAmount,
					null, null);
		} else if (creditCardScheduleEntity.getInterestAmount() < amount) {
			amount -= creditCardScheduleEntity.getInterestAmount();
			balanceReturned += processCrediCardLateAmount(creditCardScheduleEntity, amount);
		} else if (creditCardScheduleEntity.getInterestAmount() == amount) {
			balanceReturned += processCrediCardLateAmount(creditCardScheduleEntity, amount);
		}

		return balanceReturned;
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
	 * @return balance returned.
	 */
	private Double processCrediCardLateAmount(CreditCardScheduleEntity creditCardScheduleEntity,
			Double amount) {
		Double balanceReturned = 0.00;

		if (creditCardScheduleEntity.getLateAmount() > 0) {
			if (creditCardScheduleEntity.getLateAmount() > amount) {
				Double lateAmount = creditCardScheduleEntity.getLateAmount() - amount;
				balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00,
						lateAmount, null);
			} else if (creditCardScheduleEntity.getLateAmount() < amount) {
				balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00,
						true);
			} else if (creditCardScheduleEntity.getLateAmount() == amount) {
				balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00,
						true);
			}
		} else {
			balanceReturned += paidCreditCardDebt(creditCardScheduleEntity, 0.00, 0.00, 0.00, null);
		}

		return balanceReturned;
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
		creditScheduleEntity.setLateAmount(
				lateAmount != null ? lateAmount : creditScheduleEntity.getLateAmount());

		creditScheduleEntity
				.setInterestAmount(interestAmount != null ? interestAmount
						: creditScheduleEntity.getInterestAmount());

		creditScheduleEntity.setPrincipalAmount(
				principalAmount != null ? principalAmount
						: creditScheduleEntity.getPrincipalAmount());

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
	 * @return balance returned.
	 */
	private Double paidCreditCardDebt(CreditCardScheduleEntity creditCardScheduleEntity,
			Double principalAmount,
			Double interestAmount, Double lateAmount, Boolean paid) {
		Double balanceReturned = creditCardScheduleEntity.getPrincipalAmount();
		creditCardScheduleEntity.setPaid(paid != null ? paid : creditCardScheduleEntity.getPaid());

		creditCardScheduleEntity
				.setLateAmount(
						lateAmount != null ? lateAmount : creditCardScheduleEntity.getLateAmount());

		creditCardScheduleEntity.setInterestAmount(
				interestAmount != null ? interestAmount
						: creditCardScheduleEntity.getInterestAmount());

		creditCardScheduleEntity.setPrincipalAmount(
				principalAmount != null ? principalAmount
						: creditCardScheduleEntity.getPrincipalAmount());

		creditCardScheduleRepository.save(creditCardScheduleEntity);
		return balanceReturned;
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
