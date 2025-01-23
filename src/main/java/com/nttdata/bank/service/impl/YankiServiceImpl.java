package com.nttdata.bank.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.entity.YankiEntity;
import com.nttdata.bank.mapper.YankiMapper;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.YankiResponse;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.service.CustomerService;
import com.nttdata.bank.service.YankiService;
import com.nttdata.bank.util.Constants;

/**
 * YankiServiceImpl is an implementation class for YankiService interface that
 * provides a service for managing Yanki accounts.
 */
@Service
public class YankiServiceImpl implements YankiService {

	private static final Logger logger = LoggerFactory.getLogger(YankiServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private YankiRepository yankiRepository;

	/**
	 * Creates a new Yanki account.
	 *
	 * @param yankiRequest the request object containing Yanki account information
	 * @return YankiResponse the response object containing the saved Yanki account
	 *         information
	 */
	@Override
	public YankiResponse createYanki(YankiRequest yankiRequest) {
		logger.debug("Registering yanki: {}", yankiRequest);

		Optional.ofNullable(
				yankiRepository.existsByDocumentNumberAndIsActiveTrue(yankiRequest.getDocumentNumber()).block())
				.filter(Boolean::booleanValue).ifPresent(exists -> {
					throw new IllegalArgumentException("The customer already has a Yanki account.");
				});

		YankiEntity yankiEntity = YankiMapper.mapperToEntity(yankiRequest);
		yankiEntity.setCreateDate(LocalDateTime.now());
		yankiEntity.setIsActive(true);

		CompletableFuture<YankiEntity> yankiFuture = CompletableFuture.supplyAsync(() -> {
			Optional<CustomerEntity> customerOptional = Optional.ofNullable(customerRepository
					.findByDocumentNumberAndIsActiveTrue(yankiRequest.getDocumentNumber()).toFuture().join());

			customerOptional.ifPresentOrElse(customer -> {
				if (!customer.getPhoneNumber().equalsIgnoreCase(yankiRequest.getPhoneNumber())) {

					List<AccountEntity> accounts = accountRepository
							.findByHolderDocContainingAndIsActiveTrue(yankiRequest.getDocumentNumber())
							.filter(account -> account.getAccountType()
									.equalsIgnoreCase(Constants.ACCOUNT_TYPE_CODE_SAVINGS))
							.collectList().toFuture().join();

					if (!accounts.isEmpty()) {
						yankiEntity.setAccountNumber(accounts.get(0).getAccountNumber());
					} else {
						createAndSetYankiAccount(yankiEntity, yankiRequest);
					}
				} else {
					throw new IllegalArgumentException("The phone number is different from the registered one.");
				}
			}, () -> {
				createCustomerAndAccount(yankiEntity, yankiRequest);
			});

			return yankiEntity;
		});

		logger.debug("Registering yanki sucessfully: {}", yankiRequest);

		return YankiMapper.mapperToResponse(
				yankiFuture.thenApply(entity -> yankiRepository.save(entity).toFuture().join()).join());
	}

	/**
	 * Creates and sets a Yanki account based on the provided Yanki request.
	 *
	 * @param yankiEntity  the Yanki entity to be updated
	 * @param yankiRequest the Yanki request containing account information
	 */
	private void createAndSetYankiAccount(YankiEntity yankiEntity, YankiRequest yankiRequest) {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_CODE_YANKI);
		accountRequest.setHolderDoc(List.of(yankiRequest.getDocumentNumber()));
		accountRequest.setCurrency(Constants.CURRENCY_SOL);
		accountRequest.setOpeningAmount(0.00);
		AccountResponse accountResponse = accountsService.registerAccount(accountRequest);
		yankiEntity.setAccountNumber(accountResponse.getAccountNumber());
	}

	/**
	 * Creates a new customer and sets a Yanki account based on the provided Yanki
	 * request.
	 *
	 * @param yankiEntity  the Yanki entity to be updated
	 * @param yankiRequest the Yanki request containing customer and account
	 *                     information
	 */
	private void createCustomerAndAccount(YankiEntity yankiEntity, YankiRequest yankiRequest) {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setDocumentType(yankiRequest.getDocumentType());
		customerRequest.setDocumentNumber(yankiRequest.getDocumentNumber());
		customerRequest.setPhoneNumber(yankiRequest.getPhoneNumber());

		if (Constants.DOCUMENT_TYPE_CE.equalsIgnoreCase(yankiRequest.getDocumentType())
				|| Constants.DOCUMENT_TYPE_DNI.equalsIgnoreCase(yankiRequest.getDocumentType())) {
			customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
			customerRequest.setFullName(yankiRequest.getName());
		} else if (Constants.DOCUMENT_TYPE_RUC.equalsIgnoreCase(yankiRequest.getDocumentType())) {
			customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
			customerRequest.setCompanyName(yankiRequest.getName());
		}

		customerService.createCustomer(customerRequest);
		createAndSetYankiAccount(yankiEntity, yankiRequest);
	}

	/**
	 * Retrieves all active Yanki accounts.
	 *
	 * @return a list of Yanki response objects containing Yanki account information
	 */
	@Override
	public List<YankiResponse> findAllYanki() {
		logger.debug("Finding all accounts");

		List<YankiEntity> yankis = yankiRepository.findAllByIsActiveTrue().collectList().toFuture().join();
		List<YankiResponse> response = yankis.stream().map(entity -> {
			YankiResponse yankiResponse = new YankiResponse();
			yankiResponse.setName(entity.getName());
			yankiResponse.setPhoneNumber(entity.getPhoneNumber());
			return yankiResponse;
		}).collect(Collectors.toList());

		logger.info("All accounts retrieved successfully");
		return response;
	}

	/**
	 * Updates an existing Yanki account with new information.
	 *
	 * @param phoneNumber        the phone number associated with the Yanki account
	 *                           to be updated
	 * @param yankiUpdateRequest the request object containing the updated Yanki
	 *                           account information
	 * @return YankiResponse the response object containing the updated Yanki
	 *         account information
	 */
	@Override
	public YankiResponse updateYanki(String phoneNumber, YankiUpdateRequest yankiUpdateRequest) {
		YankiEntity yankiEntity = Optional
				.ofNullable(yankiRepository.findByPhoneNumberAndIsActiveTrue(phoneNumber).toFuture().join())
				.orElseThrow(() -> new IllegalArgumentException(
						"YankiEntity with phone number " + phoneNumber + " does not exist."));

		yankiEntity.setUpdateDate(LocalDateTime.now());

		Optional.of(accountRepository.findByHolderDocContainingAndIsActiveTrue(yankiEntity.getDocumentNumber())
				.collectList().toFuture().join().stream().findAny().isPresent()).filter(Boolean::booleanValue)
				.ifPresentOrElse(exists -> yankiEntity.setAccountNumber(yankiUpdateRequest.getAccountNumber()), () -> {
					throw new IllegalArgumentException(
							"YankiEntity with phone number " + phoneNumber + " does not exist.");
				});

		YankiEntity savedEntity = yankiRepository.save(yankiEntity).toFuture().join();
		YankiResponse yankiResponse = new YankiResponse();
		yankiResponse.setName(savedEntity.getName());
		yankiResponse.setPhoneNumber(savedEntity.getPhoneNumber());
		return yankiResponse;
	}

	/**
	 * Deletes a Yanki account based on the provided phone number.
	 *
	 * @param phoneNumber the phone number associated with the Yanki account
	 */
	@Override
	public void deleteYanki(String phoneNumber) {
		logger.debug("Deleting yanki: ", phoneNumber);

		Optional.ofNullable(yankiRepository.findByPhoneNumberAndIsActiveTrue(phoneNumber).toFuture().join())
				.ifPresentOrElse(entity -> {
					entity.setDeleteDate(LocalDateTime.now());
					entity.setIsActive(false);
					yankiRepository.save(entity);
				}, () -> {
					throw new IllegalArgumentException(
							"YankiEntity with phone number " + phoneNumber + " does not exist.");
				});
	}

}
