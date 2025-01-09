package com.nttdata.bank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.mapper.AccountMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.util.Constants;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountsServiceImpl implements AccountsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public AccountResponse registerAccount(AccountRequest accountRequest) {
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
		return AccountMapper.mapperToResponse(accountEntity);
	}

	private void validateAccountRequest(AccountRequest accountRequest) {
		if (accountRequest.getCustomerId() == null || accountRequest.getCustomerId().isEmpty()) {
			throw new IllegalArgumentException("CustomerId is mandatory.");
		}

		if (accountRequest.getAuthorizedSignatory() != null
				&& Constants.PERSON_TYPE_PERSONAL.equals(getPersonType(accountRequest.getCustomerId().get(0)))) {
			throw new IllegalArgumentException("AuthorizedSignatory must be null for personal customers.");
		}

		for (String customerId : accountRequest.getCustomerId()) {
			List<AccountEntity> customerAccounts = accountRepository.findByCustomerIdAndIsActiveTrue(customerId);
			String personType = getPersonType(customerId);

			if (Constants.PERSON_TYPE_PERSONAL.equals(personType)) {
				if (accountRequest.getCustomerId().size() > 1) {
					throw new IllegalArgumentException("A personal customer can only have one customer ID.");
				}
				long personalAccountCount = customerAccounts.stream()
						.filter(account -> Constants.ACCOUNT_TYPE_SAVINGS.equals(account.getAccountType())
								|| Constants.ACCOUNT_TYPE_CHECKING.equals(account.getAccountType())
								|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(account.getAccountType()))
						.count();
				if (personalAccountCount > 0 && (Constants.ACCOUNT_TYPE_SAVINGS.equals(accountRequest.getAccountType())
						|| Constants.ACCOUNT_TYPE_CHECKING.equals(accountRequest.getAccountType())
						|| Constants.ACCOUNT_TYPE_FIXED_TERM.equals(accountRequest.getAccountType()))) {
					throw new IllegalArgumentException("A personal customer can only have one account of each type.");
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

	private String generateUniqueAccountNumber(String accountType) {
		String accountNumber;
		do {
			accountNumber = generateAccountNumber(accountType);
		} while (accountRepository.existsByAccountNumberAndIsActiveTrue(accountNumber));
		return accountNumber;
	}

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

	private String getPersonType(String customerId) {
		CustomerEntity customer = customerRepository.findByIdAndIsActiveTrue(customerId).orElse(null);

		if (customer != null) {
			return customer.getPersonType();
		}

		throw new IllegalArgumentException("Customer not found with ID: " + customerId);
	}

	@Override
	public BalanceResponse checkBalance(String accountNumber) {
		AccountEntity accountEntity = accountRepository.findByAccountNumberAndIsActiveTrue(accountNumber);

		if (accountEntity == null) {
			throw new IllegalArgumentException("Account not found with number: " + accountNumber);
		}

		BalanceResponse balanceResponse = new BalanceResponse();
		balanceResponse.setAccountNumber(accountEntity.getAccountNumber());
		balanceResponse.setAmount(accountEntity.getAmount());
		balanceResponse.setAllowWithdrawals(accountEntity.getAllowWithdrawals());
		return balanceResponse;
	}

	@Override
	public List<AccountResponse> findAllAccounts() {
		return accountRepository.findAllByIsActiveTrue().stream().map(AccountMapper::mapperToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public AccountResponse updateAccount(String accountId) {
		AccountEntity accountEntity = accountRepository.findByIdAndIsActiveTrue(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		
		accountEntity.setAllowWithdrawals(!accountEntity.getAllowWithdrawals());
		accountEntity.setUpdateDate(LocalDateTime.now());
		accountEntity = accountRepository.save(accountEntity);
		return AccountMapper.mapperToResponse(accountEntity);
	}

	@Override
	public void deleteAccount(String accountId) {
		AccountEntity accountEntity = accountRepository.findByIdAndIsActiveTrue(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		
		accountEntity.setDeleteDate(LocalDateTime.now());
		accountEntity = accountRepository.save(accountEntity);
		accountEntity.setIsActive(true);
	}
}
