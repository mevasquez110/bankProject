package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.service.impl.AccountsServiceImpl;
import com.nttdata.bank.util.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class AccountServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private CreditCardRepository creditCardRepository;

	@Mock
	private CreditRepository creditRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private OperationService operationService;

	@Mock
	private DebitCardRepository debitCardRepository;

	@Mock
	private YankiRepository yankiRepository;

	@InjectMocks
	private AccountsServiceImpl accountsService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidAccountRequestWithMissingAccountType() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setOpeningAmount(100.0);
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	public void testInvalidAccountRequestWithNegativeOpeningAmount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_SAVINGS);
		accountRequest.setOpeningAmount(-50.0);
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	public void testUpdateAccountWithInvalidRequest() {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber("12345678914");
		updateAccountRequest.setAmount(-200.0);
		assertFalse(validator.validate(updateAccountRequest).isEmpty());
	}

	@Test
	public void testInvalidTypeAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType("INVALID_TYPE ACCOUNT");
		accountRequest.setOpeningAmount(-50.0);
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	public void testInvalid() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setOpeningAmount(-50.0);
		accountRequest.setHolderDoc(Arrays.asList());
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	@Order(15)
	public void testInvalidEmpty() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType("");
		accountRequest.setOpeningAmount(-50.0);
		accountRequest.setHolderDoc(Arrays.asList());
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	public void createAccount_existsCreditCard() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(false, true);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});
	}

	@Test
	public void createAccount_existsCredit() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, false);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});
	}

	@Test
	public void createAccount_existsCreditAndCreditCard() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_PYME);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(false, false);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});
	}

	@Test
	public void createAccount_CustomerEmpty() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		when(customerRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.empty());

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersPersonal() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersSignatories() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"),
				Arrays.asList("234"),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		getCustomer("123", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux
						.fromIterable(Arrays.asList(getAccountEntity("123", Arrays.asList("123"),
								Arrays.asList("123"), Constants.ACCOUNT_TYPE_CHECKING))));

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_DocumentType() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_PERSONAL);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux
						.fromIterable(Arrays.asList(getAccountEntity("123", Arrays.asList("123"),
								Arrays.asList("123"), Constants.ACCOUNT_TYPE_CHECKING))));

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_accountType1() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		getCustomer("123", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux
						.fromIterable(Arrays.asList(getAccountEntity("123", Arrays.asList("123"),
								Arrays.asList("123"), Constants.ACCOUNT_TYPE_SAVINGS))));

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_accountType2() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123"), Arrays.asList(),
				Constants.ACCOUNT_TYPE_PYME);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		getCustomer("123", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays.asList(
						getAccountEntity("123", Arrays.asList("123"), Arrays.asList(),
								Constants.ACCOUNT_TYPE_VIP))));

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersBusiness() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_BUSINESS);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersBusinessRequest1() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_FIXED_TERM);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersBusinessRequest2() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersBusinessRequest3() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersBusinessRequest4() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_HoldersOther() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("123", "345"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_CHECKING);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("123", Arrays.asList("123"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("123", Constants.DOCUMENT_TYPE_RUC, "INVALID");

		assertThrows(Exception.class, () -> {
			accountsService.registerAccount(accountRequest);
		});

	}

	@Test
	public void createAccount_Success1() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327", "08352364"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_CHECKING);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void createAccount_Success2() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327", "08352364"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_PYME);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_RUC, Constants.PERSON_TYPE_BUSINESS);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void createAccount_Success3() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_SAVINGS);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void createAccount_Success4() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_FIXED_TERM);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void createAccount_Success5() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_YANKI);

		assertTrue(validator.validate(accountRequest).isEmpty());

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void createAccount_Success6() {
		AccountRequest accountRequest = getAccountRequest(Arrays.asList("72900327"),
				Arrays.asList(),
				Constants.ACCOUNT_TYPE_VIP);

		assertTrue(validator.validate(accountRequest).isEmpty());

		validVipOrPyme(true, true);

		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		getCustomer("36548214", Constants.DOCUMENT_TYPE_DNI, Constants.PERSON_TYPE_PERSONAL);
		existsAccountNumber();
		saveAccount();
		makeDeposit();
		accountsService.registerAccount(accountRequest);
	}

	@Test
	public void checkBalance_NotFound() {
		when(accountRepository.findByAccountNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.empty());

		assertThrows(Exception.class, () -> {
			accountsService.checkBalance("123");
		});
	}

	@Test
	public void checkBalance_Success() {
		getAccountRepo();
		accountsService.checkBalance("123");
	}

	@Test
	public void listCustomer_success() {
		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
								Constants.ACCOUNT_TYPE_CHECKING))));

		assertNotNull(accountsService.findAllAccounts("123"));
	}

	@Test
	public void updateAccount_notFound() {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber("12345678914785");
		updateAccountRequest.setAmount(100.00);

		assertTrue(validator.validate(updateAccountRequest).isEmpty());

		when(accountRepository.findByAccountNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.empty());

		assertThrows(Exception.class, () -> {
			accountsService.updateAccount(updateAccountRequest);
		});
	}

	@Test
	public void updateAccount_Success() {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber("12345678914785");
		updateAccountRequest.setAmount(100.00);
		assertTrue(validator.validate(updateAccountRequest).isEmpty());
		getAccountRepo();
		saveAccount();
		accountsService.updateAccount(updateAccountRequest);
	}

	@Test
	public void deleteAccount_debitCardPrimary() {
		existsDebitCard(true);

		assertThrows(Exception.class, () -> {
			accountsService.deleteAccount("123");
		});
	}

	@Test
	public void deleteAccount_notFound() {
		existsDebitCard(false);

		when(accountRepository.findByAccountNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.empty());

		assertThrows(Exception.class, () -> {
			accountsService.deleteAccount("123");
		});
	}

	@Test
	public void deleteAccount_Yanki() {
		existsDebitCard(false);
		getAccountRepo();
		existsYanki(true);
		saveAccount();

		assertThrows(Exception.class, () -> {
			accountsService.deleteAccount("123");
		});
	}

	@Test
	public void deleteAccount_Success() {
		existsDebitCard(false);
		getAccountRepo();
		existsYanki(false);
		saveAccount();
		accountsService.deleteAccount("123");
	}

	private void validVipOrPyme(Boolean existsCreditCard, Boolean existsCredit) {
		existsCreditCard(existsCredit);
		existsCredit(existsCredit);
	}

	private void saveAccount() {
		when(accountRepository.save(any(AccountEntity.class))).thenAnswer(invocation -> {
			AccountEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

	private CustomerEntity getCustomer(String documentNumber, String documentType,
			String personType) {
		CustomerEntity customerEntity = getCustomerEntity(documentNumber, documentType, personType);

		when(customerRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(customerEntity));

		return customerEntity;
	}

	private AccountEntity getAccountRepo() {
		AccountEntity accountEntity = getAccountEntity("123", Arrays.asList("123"), null,
				Constants.ACCOUNT_TYPE_CHECKING);

		when(accountRepository.findByAccountNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(accountEntity));

		return accountEntity;
	}

	private void existsCreditCard(Boolean exists) {
		when(creditCardRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(exists));
	}

	private void existsYanki(Boolean exists) {
		when(yankiRepository.existsByAccountNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(exists));
	}

	private void existsDebitCard(Boolean exists) {
		when(debitCardRepository.existsByPrimaryAccountAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(exists));
	}

	private void existsAccountNumber() {
		when(accountRepository.existsByAccountNumber(any(String.class)))
				.thenReturn(Mono.just(false));
	}

	private void existsCredit(Boolean exists) {
		when(creditRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(exists));
	}

	private void makeDeposit() {
		when(operationService.makeDeposit(any(DepositRequest.class)))
				.thenReturn(Mono.just(new TransactionResponse()));
	}

	private CustomerEntity getCustomerEntity(String value, String documentType, String personType) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setId(value);
		customerEntity.setFullName("maria vasquez");
		customerEntity.setEmail(value + "@gmail.com");
		customerEntity.setAddress("calle falsa" + value);
		customerEntity.setPhoneNumber(value);
		customerEntity.setPersonType(personType);
		customerEntity.setDocumentNumber(value);
		customerEntity.setDocumentType(documentType);
		customerEntity.setIsActive(true);
		return customerEntity;
	}

	private AccountRequest getAccountRequest(List<String> holders, List<String> signatories,
			String accountType) {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(accountType);
		accountRequest.setOpeningAmount(300.00);
		accountRequest.setHolderDoc(holders);
		accountRequest.setAuthorizedSignatoryDoc(signatories);
		return accountRequest;
	}

	private AccountEntity getAccountEntity(String value, List<String> holders,
			List<String> signatories,
			String accountType) {
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setAccountType(accountType);
		accountEntity.setId(value);
		accountEntity.setAccountNumber(value + value + value);
		accountEntity.setMonthlyTransactionLimit(10);
		accountEntity.setWithdrawalDepositDate(LocalDateTime.now());
		accountEntity.setCurrency(Constants.CURRENCY_SOL);
		accountEntity.setAmount(100.00);
		accountEntity.setCommissionPending(0.00);
		accountEntity.setHolderDoc(holders);
		accountEntity.setAuthorizedSignatoryDoc(signatories);
		accountEntity.setCreateDate(LocalDateTime.now());
		accountEntity.setIsActive(true);
		return accountEntity;
	}

}
