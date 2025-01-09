package com.nttdata.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.client.TransactionClient;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.AccountMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.TransactionRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.util.Constants;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * * AccountsServiceImpl is the implementation class for the AccountsService
 * interface. * This class provides the actual logic for handling
 * account-related operations such as registering an account, * checking account
 * balance, finding all accounts, updating an account, and deleting an account.
 */

@Service
public class AccountsServiceImpl implements AccountsService {

	private static final Logger logger = LoggerFactory.getLogger(AccountsServiceImpl.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionClient transactionClient;

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

		if (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(10);
		} else if (Constants.ACCOUNT_TYPE_CHECKING.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(15.0);
			accountEntity.setMonthlyTransactionLimit(null);
		} else if (Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType())) {
			accountEntity.setMaintenanceCommission(0.0);
			accountEntity.setMonthlyTransactionLimit(1);
		}

		accountEntity.setAllowWithdrawals(true);
		accountEntity.setCreateDate(LocalDateTime.now());
		accountEntity.setIsActive(true);
		accountEntity = accountRepository.save(accountEntity);
		AccountResponse response = AccountMapper.mapperToResponse(accountEntity);
		logger.info("Account registered successfully: {}", response);
		TransactionResponse transactionResponse = makeDeposit(accountRequest, accountEntity);
		accountEntity.setAmount(transactionResponse.getAmount());
		accountEntity = accountRepository.save(accountEntity);
		return response;
	}

	private TransactionResponse makeDeposit(AccountRequest accountRequest, AccountEntity accountEntity) {
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setAccountNumber(accountEntity.getAccountNumber());
		transactionRequest.setAmount(accountRequest.getOpeningAmount());
		TransactionResponse transactionResponse = transactionClient.makeDeposit(transactionRequest);
		logger.info("Transaction registered successfully: {}", transactionResponse);
		return transactionResponse;
	}

	/**
	 * Validates the account request.
	 *
	 * @param accountRequest The account request payload
	 */
	private void validateAccountRequest(AccountRequest accountRequest) {
		if (accountRequest.getCustomerId() == null || accountRequest.getCustomerId().isEmpty()) {
			throw new IllegalArgumentException("CustomerId is mandatory.");
		}

		Optional.ofNullable(accountRequest.getAuthorizedSignatory()).filter(signatory -> Constants.PERSON_TYPE_PERSONAL
				.equals(getPersonType(accountRequest.getCustomerId().get(0)))).ifPresent(signatory -> {
					throw new IllegalArgumentException("AuthorizedSignatory must be null.");
				});

		for (String customerId : accountRequest.getCustomerId()) {
			List<AccountEntity> accounts = accountRepository.findByCustomerIdAndIsActiveTrue(customerId);
			String personType = getPersonType(customerId);

			if (Constants.PERSON_TYPE_PERSONAL.equals(personType)) {
				if (accountRequest.getCustomerId().size() > 1) {
					throw new IllegalArgumentException("A personal customer can have only one ID.");
				}

				long personalAccountCount = accounts.stream()
						.filter(account -> Constants.ACCOUNT_TYPE_SAVINGS.equals(account.getAccountType())
								|| Constants.ACCOUNT_TYPE_CHECKING.equals(account.getAccountType())
								|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(account.getAccountType()))
						.count();

				if (personalAccountCount > 0 && (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())
						|| Constants.ACCOUNT_TYPE_CHECKING.equals(accountRequest.getAccountType())
						|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType()))) {
					throw new IllegalArgumentException("A personal customer can have only one account per type.");

				}
			} else if (Constants.PERSON_TYPE_BUSINESS.equals(personType)) {
				if (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())
						|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType())) {
					throw new IllegalArgumentException(
							"Business customers cannot have savings or fixed-term accounts.");
				}
			} else {
				throw new IllegalArgumentException("Invalid person type.");
			}
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
		do {
			accountNumber = generateAccountNumber(accountType);
		} while (accountRepository.existsByAccountNumberAndIsActiveTrue(accountNumber));
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
	 * @param customerId The customer ID
	 * @return The person type
	 */
	private String getPersonType(String customerId) {
		CustomerEntity customer = customerRepository.findByIdAndIsActiveTrue(customerId).orElse(null);

		if (customer != null) {
			return customer.getPersonType();
		}

		throw new IllegalArgumentException("Customer not found with ID: " + customerId);
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
		AccountEntity accountEntity = accountRepository.findByAccountNumberAndIsActiveTrue(accountNumber);

		if (accountEntity == null) {
			throw new IllegalArgumentException("Account not found with number: " + accountNumber);
		}

		BalanceResponse balanceResponse = new BalanceResponse();
		balanceResponse.setAccountNumber(accountEntity.getAccountNumber());
		balanceResponse.setAmount(accountEntity.getAmount());
		balanceResponse.setAllowWithdrawals(accountEntity.getAllowWithdrawals());
		logger.info("Balance checked successfully: {}", balanceResponse);
		return balanceResponse;
	}

	/**
	 * Finds all accounts.
	 *
	 * @return A list of account responses
	 */
	@Override
	public List<AccountResponse> findAllAccounts() {
		logger.debug("Finding all accounts");
		List<AccountResponse> accounts = accountRepository.findByIsActiveTrue().stream()
				.map(AccountMapper::mapperToResponse).collect(Collectors.toList());
		logger.info("All accounts retrieved successfully");
		return accounts;
	}

	/**
	 * Updates an account.
	 *
	 * @param accountId The account ID
	 * @return The updated account response
	 */
	@Override
	public AccountResponse updateAccount(String accountId) {
		logger.debug("Updating account with ID: {}", accountId);
		AccountEntity accountEntity = accountRepository.findByIdAndIsActiveTrue(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		accountEntity.setAllowWithdrawals(!accountEntity.getAllowWithdrawals());
		accountEntity.setUpdateDate(LocalDateTime.now());
		accountEntity = accountRepository.save(accountEntity);
		AccountResponse response = AccountMapper.mapperToResponse(accountEntity);
		logger.info("Account updated successfully: {}", response);
		return response;
	}

	/**
	 * Deletes an account.
	 *
	 * @param accountId The account ID
	 */
	@Override
	public void deleteAccount(String accountId) {
		logger.debug("Deleting account with ID: {}", accountId);
		AccountEntity accountEntity = accountRepository.findByIdAndIsActiveTrue(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		accountEntity.setDeleteDate(LocalDateTime.now());
		accountEntity.setIsActive(true);
		accountEntity = accountRepository.save(accountEntity);
		logger.info("Account deleted successfully with ID: {}", accountId);
	}
}
