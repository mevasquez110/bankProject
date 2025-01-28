package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.service.impl.OperationServiceImpl;
import com.nttdata.bank.util.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.entity.TransactionEntity;
import com.nttdata.bank.entity.YankiEntity;
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
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.request.UpdateAccountRequest;

@SpringBootTest
public class OperationServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private DebitCardRepository debitCardRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountsService accountService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private YankiRepository yankiRepository;

	@Mock
	private CreditRepository creditRepository;

	@Mock
	private CreditService creditService;

	@Mock
	private CreditCardRepository creditCardRepository;

	@Mock
	private CreditScheduleRepository creditScheduleRepository;

	@Mock
	private CreditCardScheduleRepository creditCardScheduleRepository;

	@InjectMocks
	private OperationServiceImpl operationService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidDepositRequestWithNullAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithBothDebitCardAndAccountNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAccountNumber("987654321");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNeitherDebitCardNorAccountNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNegativeAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(-500.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithZeroAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(0.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testValidDepositRequestWithOnlyDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("");
		depositRequest.setAccountNumber("");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testValidDepositRequestTrim() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("  ");
		depositRequest.setAccountNumber("  ");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNullDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidNull() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithEmptyDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidWithdrawalRequestWithMissingDebitCardNumber() {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setDocumentNumber("12345678");
		withdrawalRequest.setAmount(500.0);
		assertFalse(validator.validate(withdrawalRequest).isEmpty());
	}

	@Test
	public void testInvalidAccountTransferRequestWithMissingAccountNumberWithdraws() {
		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberReceive("987654321");
		accountTransferRequest.setDocumentNumberWithdraws("12345678");
		accountTransferRequest.setDocumentNumberReceive("87654321");
		accountTransferRequest.setAmount(1000.0);
		assertFalse(validator.validate(accountTransferRequest).isEmpty());
	}

	@Test
	public void testInvalidMobileTransferRequestWithMissingMobileNumberWithdraws() {
		MobileTransferRequest mobileTransferRequest = new MobileTransferRequest();
		mobileTransferRequest.setMobileNumberReceive("987654321");
		mobileTransferRequest.setDocumentNumberWithdraws("12345678");
		mobileTransferRequest.setDocumentNumberReceive("87654321");
		mobileTransferRequest.setAmount(200.0);
		assertFalse(validator.validate(mobileTransferRequest).isEmpty());
	}

	@Test
	public void testInvalidPayCreditRequestWithMissingCreditId() {
		PayCreditRequest payCreditRequest = new PayCreditRequest();
		payCreditRequest.setAmount(1000.0);
		payCreditRequest.setDocumentNumber("12345678");
		payCreditRequest.setAccountNumber("987654321");
		assertFalse(validator.validate(payCreditRequest).isEmpty());
	}

	@Test
	public void makeDeposit_sucess1() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setAmount(100.00);
		depositRequest.setAccountNumber("12345678963214");
		depositRequest.setDocumentNumber("12345678");

		assertTrue(validator.validate(depositRequest).isEmpty());

		findAllTransactions(Flux.fromIterable(Arrays.asList(
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL,
						depositRequest.getAccountNumber(), "12345678901234"),
				getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234"))));

		uniqueOperationNumber(Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
				depositRequest.getAccountNumber(),
				"12345678901234")));
		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_PERSONAL)));
		saveTransaction();
		updateAccount();
		operationService.makeDeposit(depositRequest);
	}

	@Test
	public void makeDeposit_success2() {
		DepositRequest depositRequest = getDepositRequest();
		assertTrue(validator.validate(depositRequest).isEmpty());
		findAllTransactions(Flux.empty());
		getDebitCard(Mono.just(getDebitCardEntity()));

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_BUSINESS)));
		saveTransaction();
		updateAccount();
		operationService.makeDeposit(depositRequest);
	}

	@Test
	public void makeDeposit_notdebitcard() {
		DepositRequest depositRequest = getDepositRequest();
		assertTrue(validator.validate(depositRequest).isEmpty());
		findAllTransactions(Flux.empty());
		getDebitCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			operationService.makeDeposit(depositRequest);
		});
	}

	@Test
	public void makeDeposit_CustomerEmpty() {
		DepositRequest depositRequest = getDepositRequest();
		assertTrue(validator.validate(depositRequest).isEmpty());
		findAllTransactions(Flux.empty());

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
						depositRequest.getAccountNumber(), "12345678901234")));

		getCustomer(Mono.empty());

		assertThrows(Exception.class, () -> {
			operationService.makeDeposit(depositRequest);
		});
	}

	@Test
	public void makeWithdrawal_notexist() {
		WithdrawalRequest withdrawalRequest = getWithdrawalRequest();
		assertTrue(validator.validate(withdrawalRequest).isEmpty());
		getDebitCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			operationService.makeWithdrawal(withdrawalRequest);
		});
	}

	@Test
	public void makeMobileTransfer_notexist() {
		MobileTransferRequest mobileTransferRequest = getMobileTransferRequest();
		assertTrue(validator.validate(mobileTransferRequest).isEmpty());
		getByPhone(Mono.empty());

		assertThrows(Exception.class, () -> {
			operationService.makeMobileTransfer(mobileTransferRequest);
		});
	}

	@Test
	public void makeMobileTransfer_success() {
		MobileTransferRequest mobileTransferRequest = getMobileTransferRequest();
		assertTrue(validator.validate(mobileTransferRequest).isEmpty());
		getByPhone(Mono.just(getYankiEntity()));

		existsGreather(Mono.just(false));

		uniqueOperationNumber(Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
				"12345678914785", "12547856985236")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_PERSONAL)));
		saveTransaction();
		updateAccount();
		operationService.makeMobileTransfer(mobileTransferRequest);
	}

	@Test
	public void makeAccountTransfer_notexist() {
		AccountTransferRequest accountTransferRequest = getAccountTransfer();
		assertTrue(validator.validate(accountTransferRequest).isEmpty());
		existsGreather(Mono.just(true));

		assertThrows(Exception.class, () -> {
			operationService.makeAccountTransfer(accountTransferRequest);
		});
	}

	@Test
	public void makeAccountTransfer_notbalance() {
		AccountTransferRequest accountTransferRequest = getAccountTransfer();
		assertTrue(validator.validate(accountTransferRequest).isEmpty());
		existsGreather(Mono.just(true));

		assertThrows(Exception.class, () -> {
			operationService.makeAccountTransfer(accountTransferRequest);
		});
	}

	@Test
	public void makeAccountTransfer_sucess() {
		AccountTransferRequest accountTransferRequest = getAccountTransfer();
		assertTrue(validator.validate(accountTransferRequest).isEmpty());
		existsGreather(Mono.just(false));

		uniqueOperationNumber(Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_DEPOSIT,
				accountTransferRequest.getAccountNumberReceive(),
				accountTransferRequest.getDocumentNumberWithdraws())));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_PERSONAL)));
		saveTransaction();
		updateAccount();
		operationService.makeAccountTransfer(accountTransferRequest);
	}

	@Test
	public void makeWithdrawal_sucess() {
		WithdrawalRequest withdrawalRequest = getWithdrawalRequest();
		assertTrue(validator.validate(withdrawalRequest).isEmpty());
		getDebitCard(Mono.just(getDebitCardEntity()));
		findAllTransactions(Flux.empty());
		existsGreather(Mono.just(true));

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL, "123",
						"12345678901234")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_BUSINESS)));
		saveTransaction();
		updateAccount();
		operationService.makeWithdrawal(withdrawalRequest);
	}

	@Test
	public void makeWithdrawal_balance() {
		WithdrawalRequest withdrawalRequest = getWithdrawalRequest();
		assertTrue(validator.validate(withdrawalRequest).isEmpty());
		getDebitCard(Mono.just(getDebitCardEntity()));
		findAllTransactions(Flux.empty());
		existsGreather(Mono.just(false));

		assertThrows(Exception.class, () -> {
			operationService.makeWithdrawal(withdrawalRequest);
		});
	}

	@Test
	public void payCredit_notCredit() {
		PayCreditRequest payCreditRequest = getPayCredit(100.00);
		assertTrue(validator.validate(payCreditRequest).isEmpty());
		existsCreditId(Mono.just(true));

		assertThrows(Exception.class, () -> {
			operationService.payCredit(payCreditRequest);
		});
	}

	@Test
	public void payCredit_greather() {
		PayCreditRequest payCreditRequest = getPayCredit(100.00);
		assertTrue(validator.validate(payCreditRequest).isEmpty());
		existsCreditId(Mono.just(false));
		existsGreather(Mono.just(true));

		assertThrows(Exception.class, () -> {
			operationService.payCredit(payCreditRequest);
		});
	}

	@Test
	public void payCredit_exce() {
		PayCreditRequest payCreditRequest = getPayCredit(500.00);
		assertTrue(validator.validate(payCreditRequest).isEmpty());
		existsCreditId(Mono.just(false));
		existsGreather(Mono.just(false));
		updateAccount();

		getListCreditScheduleLess(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		getScheduleDateAfter(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL, "123",
						"12345678901234")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_BUSINESS)));

		saveTransaction();

		assertThrows(Exception.class, () -> {
			operationService.payCredit(payCreditRequest);
		});

	}

	@Test
	public void payCredit_totaldebt() {
		PayCreditRequest payCreditRequest = getPayCredit(240.00);
		assertTrue(validator.validate(payCreditRequest).isEmpty());
		existsCreditId(Mono.just(false));
		existsGreather(Mono.just(false));
		updateAccount();

		getListCreditScheduleLess(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		getScheduleDateAfter(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL, "123",
						"12345678901234")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_BUSINESS)));

		saveTransaction();
		existsById(Mono.just(true));
		operationService.payCredit(payCreditRequest);
	}

	@Test
	public void payCredit_share() {
		PayCreditRequest payCreditRequest = getPayCredit(120.00);
		assertTrue(validator.validate(payCreditRequest).isEmpty());
		existsCreditId(Mono.just(false));
		existsGreather(Mono.just(false));
		updateAccount();

		getListCreditScheduleLess(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		getScheduleDateAfter(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		uniqueOperationNumber(
				Mono.just(getTransactionEntity(Constants.TRANSACTION_TYPE_WITHDRAWAL, "123",
						"12345678901234")));

		getCustomer(Mono.just(getCustomerEntity("123",
				Constants.PERSON_TYPE_BUSINESS)));

		saveTransaction();
		existsById(Mono.just(false));
		operationService.payCredit(payCreditRequest);
	}

	private void getScheduleDateAfter(Flux<CreditScheduleEntity> creditScheduleEntity) {
		when(creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateAfter(any(String.class),
						any(LocalDateTime.class)))
				.thenReturn(creditScheduleEntity);
	}

	@Test
	public void getProducts_success() {
		findAllByDocumentNumber(Flux.fromIterable(Arrays
				.asList(getCreditEntity())));

		findAllByDocumentNumberCard(Flux.fromIterable(Arrays
				.asList(getCreditCardEntity(true))));

		findByHolder(Flux.fromIterable(Arrays
				.asList(getAccountEntity("123", Arrays.asList("123"), null,
						Constants.ACCOUNT_TYPE_CHECKING))));

		operationService.getProducts("123");
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

	private CreditCardEntity getCreditCardEntity(Boolean allowConsumption) {
		CreditCardEntity creditCardEntity = new CreditCardEntity();
		creditCardEntity.setId("123");
		creditCardEntity.setAllowConsumption(allowConsumption);
		creditCardEntity.setAvailableCredit(100.00);
		creditCardEntity.setPaymentDay(5);
		creditCardEntity.setCreditCardNumber("1234567897412589");
		return creditCardEntity;
	}

	private CreditEntity getCreditEntity() {
		CreditEntity creditEntity = new CreditEntity();
		creditEntity.setId("123");
		return creditEntity;
	}

	private CreditScheduleEntity geCreditScheduleEntity() {
		CreditScheduleEntity creditScheduleEntity = new CreditScheduleEntity();
		creditScheduleEntity.setId("123");
		creditScheduleEntity.setPaymentDate(LocalDate.now());
		creditScheduleEntity.setCurrentDebt(120.00);
		creditScheduleEntity.setLateAmount(20.00);
		creditScheduleEntity.setInterestAmount(12.00);
		creditScheduleEntity.setPrincipalAmount(30.00);
		creditScheduleEntity.setPaid(false);
		creditScheduleEntity.setPaymentDate(LocalDate.now());
		return creditScheduleEntity;
	}

	private PayCreditRequest getPayCredit(Double amount) {
		PayCreditRequest payCreditRequest = new PayCreditRequest();
		payCreditRequest.setAmount(amount);
		payCreditRequest.setDocumentNumber("12345678");
		payCreditRequest.setAccountNumber("12345678941256");
		payCreditRequest.setCreditId("123");
		return payCreditRequest;
	}

	private DepositRequest getDepositRequest() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setAmount(100.00);
		depositRequest.setDebitCardNumber("1212345678963214");
		depositRequest.setDocumentNumber("12345678");
		return depositRequest;
	}

	private MobileTransferRequest getMobileTransferRequest() {
		MobileTransferRequest mobileTransferRequest = new MobileTransferRequest();
		mobileTransferRequest.setAmount(100.00);
		mobileTransferRequest.setMobileNumberReceive("987456321");
		mobileTransferRequest.setDocumentNumberReceive("12345678");
		mobileTransferRequest.setMobileNumberWithdraws("985263145");
		mobileTransferRequest.setDocumentNumberWithdraws("36541258");
		return mobileTransferRequest;
	}

	private YankiEntity getYankiEntity() {
		YankiEntity yankiEntity = new YankiEntity();
		yankiEntity.setAccountNumber("12345678914785");
		yankiEntity.setDocumentNumber("12345678");
		yankiEntity.setId("123");
		yankiEntity.setName("joe doe");
		yankiEntity.setPhoneNumber("987456321");
		return yankiEntity;
	}

	private AccountTransferRequest getAccountTransfer() {
		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAmount(100.00);
		accountTransferRequest.setAccountNumberReceive("14256398754125");
		accountTransferRequest.setDocumentNumberReceive("12345678");
		accountTransferRequest.setAccountNumberWithdraws("14528745698523");
		accountTransferRequest.setDocumentNumberWithdraws("36541258");
		return accountTransferRequest;
	}

	private WithdrawalRequest getWithdrawalRequest() {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setAmount(100.00);
		withdrawalRequest.setDebitCardNumber("1212345678963214");
		withdrawalRequest.setDocumentNumber("12345678");
		return withdrawalRequest;
	}

	private CustomerEntity getCustomerEntity(String value, String type) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setId(value);
		customerEntity.setCompanyName("empresa sac");
		customerEntity.setFullName("maria vasquez");
		customerEntity.setEmail(value + "@gmail.com");
		customerEntity.setAddress("calle falsa" + value);
		customerEntity.setPhoneNumber(value);
		customerEntity.setPersonType(type);
		customerEntity.setDocumentNumber(value);
		customerEntity.setIsActive(true);
		return customerEntity;
	}

	private void getCustomer(Mono<CustomerEntity> customerEntity) {
		when(customerRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(customerEntity);
	}

	private void saveTransaction() {
		when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(
				invocation -> {
					TransactionEntity entity = invocation.getArgument(0);
					return Mono.just(entity);
				});
	}

	private DebitCardEntity getDebitCardEntity() {
		DebitCardEntity debitCardEntity = new DebitCardEntity();
		debitCardEntity.setPrimaryAccount("12345678996325");
		debitCardEntity.setDocumentNumber("12345678");
		return debitCardEntity;
	}

	private TransactionEntity getTransactionEntity(String transactionType, String accountReceive,
			String accountWithdraws) {
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setTransactionType(transactionType);
		transactionEntity.setCreateDate(LocalDateTime.now());
		transactionEntity.setAccountNumberReceive(accountReceive);
		transactionEntity.setAccountNumberWithdraws(accountWithdraws);
		transactionEntity.setOperationNumber("123");
		transactionEntity.setAmount(100.00);
		return transactionEntity;
	}

	private void updateAccount() {
		when(accountService.updateAccount(any(UpdateAccountRequest.class)))
				.thenReturn(new AccountResponse());
	}

	private void desactivateCredit() {
		doNothing().when(creditService).desactivateCredit(any(String.class));
	}

	private void getDebitCard(Mono<DebitCardEntity> debitCardEntity) {
		when(debitCardRepository.findByDebitCardNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(debitCardEntity);
	}

	private void existsGreather(Mono<Boolean> exists) {
		when(accountRepository.existsByAccountNumberAndAmountGreaterThanEqual(any(
				String.class), any(Double.class))).thenReturn(exists);
	}

	private void getByPhone(Mono<YankiEntity> yankiEntity) {
		when(yankiRepository
				.findByPhoneNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(yankiEntity);
	}

	private void existsCreditId(Mono<Boolean> exists) {
		when(creditRepository.existsByIdAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void getListCreditScheduleLess(Flux<CreditScheduleEntity> creditScheduleEntity) {
		when(creditScheduleRepository.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
				any(String.class),
				any(LocalDateTime.class))).thenReturn(creditScheduleEntity);
	}

	private void existsById(Mono<Boolean> exists) {
		when(creditScheduleRepository.existsByIdAndPaidFalse(any(String.class)))
				.thenReturn(exists);
	}

	private void findAllByDocumentNumber(Flux<CreditEntity> creditEntity) {
		when(creditRepository
				.findAllByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditEntity);
	}

	private void findAllByDocumentNumberCard(Flux<CreditCardEntity> creditCardEntity) {
		when(creditCardRepository
				.findAllByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditCardEntity);
	}

	private void findByHolder(Flux<AccountEntity> accountEntity) {
		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(accountEntity);
	}

	private void findAllTransactions(Flux<TransactionEntity> transactionEntity) {
		when(transactionRepository.findAllByIsActiveTrue())
				.thenReturn(transactionEntity);
	}

	private void existsByCreditCardNumber(Mono<Boolean> exists) {
		when(creditCardRepository.existsByCreditCardNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void uniqueOperationNumber(Mono<TransactionEntity> transactionEntity) {
		when(transactionRepository.findFirstByOrderByOperationNumberDesc())
				.thenReturn(transactionEntity);
	}

	private void findLessThanEqual(Flux<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(any(String.class),
						any(LocalDate.class)))
				.thenReturn(creditCardScheduleEntity);
	}

	private void findAfter(Flux<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateAfter(any(String.class),
						any(LocalDateTime.class)))
				.thenReturn(creditCardScheduleEntity);
	}

	private void getCreditCard(Mono<CreditCardEntity> creditCardEntity) {
		when(creditCardRepository.findByCreditCardNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditCardEntity);
	}

}
