package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.AccountMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.util.Constants;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * AccountsServiceImpl is the implementation class for the AccountsService
 * interface. This class provides the actual logic for handling account-related
 * operations such as registering an account, checking account balance, finding
 * all accounts, updating an account, and deleting an account.
 */

@Service
public class AccountsServiceImpl implements AccountsService {

	private static final Logger logger = LoggerFactory.getLogger(AccountsServiceImpl.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OperationService transactionService;

	@Autowired
	private CreditCardRepository creditCardRepository;

	@Autowired
	private DebitCardRepository debitCardRepository;

	@Autowired
	private CreditRepository creditRepository;

	@Autowired
	private YankiRepository yankiRepository;

	/**
	 * Registers a new account.
	 *
	 * @param accountRequest The account request payload
	 * @return The account response
	 */

	@Override
	public AccountResponse registerAccount(AccountRequest accountRequest) {
		logger.debug("Registering account: {}", accountRequest);
		validateAccountRequest(accountRequest);
		AccountEntity accountEntity = AccountMapper.mapperToEntity(accountRequest);
		accountEntity.setAccountNumber(generateUniqueAccountNumber(accountRequest.getAccountType()));
		setAccountDetails(accountRequest, accountEntity);
		accountEntity = accountRepository.save(accountEntity).block();
		makeFirstDeposit(accountRequest, accountEntity, accountRequest.getHolderDoc().get(0));
		AccountResponse response = AccountMapper.mapperToResponse(accountEntity);
		logger.info("Account registered successfully: {}", response);
		return response;
	}

	/**
	 * Checks the balance of an account.
	 *
	 * @param accountNumber The account number
	 * @return The balance response
	 */
	@Override
	public BalanceResponse checkBalance(String accountNumber) {
		logger.debug("Checking balance for account: {}", accountNumber);

		AccountEntity accountEntity = Optional
				.ofNullable(accountRepository.findByAccountNumberAndIsActiveTrue(accountNumber).toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException("Account not found with number: " + accountNumber));

		logger.info("Balance checked successfully");
		return AccountMapper.mapperToBalanceResponse(accountEntity);
	}

	/**
	 * Finds all accounts.
	 * 
	 * @param documentNumber The document number
	 * @return A list of account responses
	 */
	@Override
	public List<AccountResponse> findAllAccounts(String documentNumber) {
		logger.info("All accounts retrieved successfully");
		return accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber)
				.map(AccountMapper::mapperToResponse).collectList().toFuture().join();
	}

	/**
	 * Updates an existing account.
	 *
	 * @param updateAccountRequest The request containing updated account details
	 * @return The response containing updated account information
	 */
	@Override
	public AccountResponse updateAccount(UpdateAccountRequest updateAccountRequest) {
		return accountRepository.findByAccountNumberAndIsActiveTrue(updateAccountRequest.getAccountNumber())
				.flatMap(accountEntity -> {
					accountEntity.setUpdateDate(LocalDateTime.now());
					accountEntity.setAmount(accountEntity.getAmount() + updateAccountRequest.getAmount());
					return accountRepository.save(accountEntity).map(savedEntity -> {
						AccountResponse response = AccountMapper.mapperToResponse(savedEntity);
						logger.info("Account updated successfully: {}", response);
						return response;
					});
				}).switchIfEmpty(Mono.error(new RuntimeException("Account not found"))).toFuture().join();
	}

	/**
	 * Deletes an account.
	 *
	 * @param accountNumber The account number
	 */
	@Override
	public void deleteAccount(String accountNumber) {
		logger.debug("Deleting account with account number: {}", accountNumber);

		Optional.ofNullable(debitCardRepository.existsByPrimaryAccountAndIsActiveTrue(accountNumber).block())
				.filter(isPrimary -> !isPrimary)
				.orElseThrow(() -> new IllegalArgumentException("The account with number " + accountNumber
						+ " is the primary account of an active debit card and cannot be deleted."));

		accountRepository.findByAccountNumberAndIsActiveTrue(accountNumber).flatMap(accountEntity -> {
			return Mono.just(yankiRepository.existsByAccountNumberAndIsActiveTrue(accountNumber).block())
					.doOnNext(existsYanki -> {
						if (existsYanki) {
							throw new IllegalArgumentException("Cannot delete account with associated Yanki");
						}
					}).thenReturn(accountEntity).flatMap(entity -> {
						entity.setDeleteDate(LocalDateTime.now());
						entity.setIsActive(false);
						return accountRepository.save(entity);
					}).doOnSuccess(savedEntity -> logger.info("Account deleted successfully with account number: {}",
							savedEntity.getAccountNumber()));
		}).switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
				.doOnError(
						error -> logger.error("Error deleting account with account number: {}", accountNumber, error))
				.toFuture().join();
	}

	/**
	 * Registers the first deposit transaction for a newly created account.
	 *
	 * This method creates a deposit transaction using the provided account request
	 * and entity, then logs the transaction and returns the transaction response.
	 *
	 * @param accountRequest The request object containing account details such as
	 *                       opening amount.
	 * @param accountEntity  The entity object representing the account for which
	 *                       the deposit is being made.
	 * @param documentNumber The document number of the customer creating the
	 *                       account.
	 * @return TransactionResponse The response object containing details of the
	 *         registered transaction.
	 */
	private TransactionResponse makeFirstDeposit(AccountRequest accountRequest, AccountEntity accountEntity,
			String documentNumber) {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setAccountNumber(accountEntity.getAccountNumber());
		depositRequest.setDocumentNumber(documentNumber);
		depositRequest.setAmount(accountRequest.getOpeningAmount());
		TransactionResponse transactionResponse = transactionService.makeDeposit(depositRequest);
		logger.info("Transaction registered successfully: {}", transactionResponse);
		return transactionResponse;
	}

	/**
	 * Validates the account request.
	 *
	 * @param accountRequest The account request payload
	 */
	private void validateAccountRequest(AccountRequest accountRequest) {
		if (accountRequest.getHolderDoc() == null || accountRequest.getHolderDoc().isEmpty()) {
			throw new IllegalArgumentException("Holder doc is mandatory.");
		}

		Optional.ofNullable(accountRequest.getAuthorizedSignatoryDoc())
				.filter(signatory -> Constants.PERSON_TYPE_PERSONAL.equals(accountRequest.getHolderDoc().stream()
						.findFirst().orElseThrow(() -> new IllegalArgumentException("Holder document list is empty."))))
				.ifPresent(signatory -> {
					throw new IllegalArgumentException("AuthorizedSignatory must be null for personal accounts.");
				});

		for (String documentNumber : accountRequest.getHolderDoc()) {
			validationPymeAndVIP(accountRequest, documentNumber);

			List<AccountEntity> accounts = accountRepository.findByHolderDocContainingAndIsActiveTrue(documentNumber)
					.collectList().toFuture().join();

			String personType = getPersonType(documentNumber);

			if (Constants.PERSON_TYPE_PERSONAL.equals(personType)) {
				typePersonalValidation(accountRequest, accounts);
			} else if (Constants.PERSON_TYPE_BUSINESS.equals(personType)) {
				typeBusinessValidation(accountRequest, documentNumber);
			} else {
				throw new IllegalArgumentException("Invalid person type.");
			}
		}
	}

	/**
	 * Validates the account request for business customers to ensure they meet the
	 * criteria.
	 *
	 * @param accountRequest the account request containing account details
	 * @param documentNumber the document number
	 * @throws IllegalArgumentException if any validation rule is violated
	 */
	private void typeBusinessValidation(AccountRequest accountRequest, String documentNumber) {
		Optional.ofNullable(customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber).toFuture().join())
				.ifPresentOrElse(customerEntity -> {
					if (!Constants.DOCUMENT_TYPE_RUC.equals(customerEntity.getDocumentType())) {
						throw new IllegalArgumentException("The holder is not registered as a business customer.");
					}

					if (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())
							|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType())) {
						throw new IllegalArgumentException(
								"Business customers cannot have savings or fixed-term accounts.");
					}

					if (Constants.ACCOUNT_TYPE_VIP.equals(accountRequest.getAccountType())) {
						throw new IllegalArgumentException("A business customer cannot have a VIP account.");
					}
				}, () -> {
					throw new IllegalArgumentException("The holder is not registered.");
				});
	}

	/**
	 * Validates the account request for personal customers to ensure they meet the
	 * criteria.
	 *
	 * @param accountRequest the account request containing account details
	 * @param accounts       the list of existing accounts associated with the
	 *                       customer's document number
	 * @throws IllegalArgumentException if any validation rule is violated
	 */
	private void typePersonalValidation(AccountRequest accountRequest, List<AccountEntity> accounts) {
		if (accountRequest.getHolderDoc().size() > 1) {
			throw new IllegalArgumentException("A personal customer can have only one ID.");
		}

		long personalAccountCount = accounts.stream()
				.filter(account -> Constants.ACCOUNT_TYPE_SAVINGS.equals(account.getAccountType())
						|| Constants.ACCOUNT_TYPE_CHECKING.equals(account.getAccountType())
						|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(account.getAccountType())
						|| Constants.ACCOUNT_TYPE_VIP.equals(account.getAccountType()))
				.count();

		if (personalAccountCount > 0 && (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())
				|| Constants.ACCOUNT_TYPE_CHECKING.equals(accountRequest.getAccountType())
				|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType())
				|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType()))) {
			throw new IllegalArgumentException("A personal customer can have only one account per type.");
		}

		if (Constants.ACCOUNT_TYPE_PYME.equals(accountRequest.getAccountType())) {
			throw new IllegalArgumentException("A personal customer cannot have a PYME account.");
		}
	}

	/**
	 * Validates if the given document number is associated with an active credit
	 * product when attempting to create an account of type VIP or PYME.
	 *
	 * @param accountRequest the account request containing account details
	 * @param documentNumber the document number to validate
	 * @throws IllegalStateException if no active credit product exists for the
	 *                               given document number
	 */
	private void validationPymeAndVIP(AccountRequest accountRequest, String documentNumber) {
		if (Constants.ACCOUNT_TYPE_VIP.equals(accountRequest.getAccountType())
				|| Constants.ACCOUNT_TYPE_PYME.equals(accountRequest.getAccountType())) {

			Mono.zip(creditCardRepository.existsByDocumentNumberAndIsActiveTrue(documentNumber),
					creditRepository.existsByDocumentNumberAndIsActiveTrue(documentNumber))
					.map(tuple -> tuple.getT1() || tuple.getT2()).blockOptional().ifPresentOrElse(hasCreditProduct -> {
						if (!hasCreditProduct) {
							throw new IllegalStateException("No credit product found for the given document number");
						}
					}, () -> {
						throw new IllegalStateException("Failed to validate credit product.");
					});
		}
	}

	/**
	 * Generates a unique account number.
	 *
	 * @param accountType The type of the account
	 * @return The generated unique account number
	 */
	private String generateUniqueAccountNumber(String accountType) {
		String accountNumber;
		boolean exists;
		do {
			accountNumber = generateAccountNumber(accountType);
			exists = accountRepository.existsByAccountNumber(accountNumber).toFuture().join();
		} while (exists);
		return accountNumber;
	}

	/**
	 * Generates an account number based on the account type.
	 *
	 * @param accountType The type of the account
	 * @return The generated account number
	 */
	private String generateAccountNumber(String accountType) {
		Random random = new Random();
		StringBuilder accountNumber = new StringBuilder(Constants.BANK_CODE);

		switch (accountType) {
		case Constants.ACCOUNT_TYPE_SAVINGS:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_SAVINGS);
			break;
		case Constants.ACCOUNT_TYPE_CHECKING:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_CHECKING);
			break;
		case Constants.ACCOUNT_TYPE_FIXED_TERM:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_FIXED_TERM);
			break;
		case Constants.ACCOUNT_TYPE_VIP:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_VIP);
			break;
		case Constants.ACCOUNT_TYPE_PYME:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_PYME);
			break;
		case Constants.ACCOUNT_TYPE_YANKI:
			accountNumber.append(Constants.ACCOUNT_TYPE_CODE_YANKI);
			break;
		default:
			throw new IllegalArgumentException("Invalid account type");
		}
		for (int i = 0; i < 10; i++) {
			accountNumber.append(random.nextInt(10));
		}
		return accountNumber.toString();
	}

	/**
	 * Retrieves the person type based on the customer ID.
	 *
	 * @param documentNumber The customer document number
	 * @return The person type
	 */
	private String getPersonType(String documentNumber) {
		return Optional.ofNullable(customerRepository.findByDocumentNumberAndIsActiveTrue(documentNumber).block())
				.map(CustomerEntity::getPersonType).orElseThrow(() -> new IllegalArgumentException(
						"Customer not found with document number: " + documentNumber));
	}

	/**
	 * Sets specific details for the account based on the requested account type.
	 *
	 * @param accountRequest The request object containing the account details
	 * @param accountEntity  The account entity to be updated with specific details
	 */
	private void setAccountDetails(AccountRequest accountRequest, AccountEntity accountEntity) {
		if (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(100);
		} else if (Constants.ACCOUNT_TYPE_CHECKING.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(15.0);
			accountEntity.setMonthlyTransactionLimit(null);
		} else if (Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(1);
		} else if (Constants.ACCOUNT_TYPE_VIP.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(15.0);
			accountEntity.setMonthlyTransactionLimit(null);
		} else if (Constants.ACCOUNT_TYPE_PYME.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(null);
		} else if (Constants.ACCOUNT_TYPE_YANKI.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(null);
		}
	}

}
