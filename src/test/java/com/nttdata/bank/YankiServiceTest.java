package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;

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
import com.nttdata.bank.service.CustomerService;
import com.nttdata.bank.service.impl.YankiServiceImpl;
import com.nttdata.bank.util.Constants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.nttdata.bank.entity.AccountEntity;
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.entity.YankiEntity;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.repository.YankiRepository;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.AccountResponse;

@SpringBootTest
public class YankiServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerService customerService;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AccountsService accountsService;

	@Mock
	private YankiRepository yankiRepository;

	@InjectMocks
	private YankiServiceImpl yankiService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidYankiRequestWithMissingName() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}

	@Test
	public void testInvalidYankiRequestWithInvalidPhoneNumber() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setName("John Doe");
		yankiRequest.setPhoneNumber("12345abc");
		yankiRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}

	@Test
	public void testInvalidDocumentType() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setName("John Doe");
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType("INVALID_DOCUMENT_TYPE");
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}

	@Test
	public void create_yankiexists() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_DNI);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(true));

		assertThrows(Exception.class, () -> {
			yankiService.createYanki(yankiRequest);
		});
	}

	@Test
	public void create_phone() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_DNI);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.just(getCustomerEntity("987654321", Constants.DOCUMENT_TYPE_DNI)));

		assertThrows(Exception.class, () -> {
			yankiService.createYanki(yankiRequest);
		});
	}

	@Test
	public void create_success1() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_DNI);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.just(getCustomerEntity("123", Constants.DOCUMENT_TYPE_DNI)));
		findByHolder(Flux.empty());
		registerAccount();
		saveYanki();
		yankiService.createYanki(yankiRequest);
	}

	@Test
	public void create_success2() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_DNI);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.just(getCustomerEntity("123", Constants.DOCUMENT_TYPE_DNI)));

		findByHolder(Flux.fromIterable(Arrays
				.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
						Constants.ACCOUNT_TYPE_CHECKING))));

		registerAccount();
		saveYanki();
		yankiService.createYanki(yankiRequest);
	}

	@Test
	public void create_success3() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_DNI);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.empty());
		registerAccount();
		saveYanki();
		yankiService.createYanki(yankiRequest);
	}

	@Test
	public void create_success4() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_CE);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.empty());
		registerAccount();
		saveYanki();
		yankiService.createYanki(yankiRequest);
	}

	@Test
	public void create_success5() {
		YankiRequest yankiRequest = getYankiRequest(Constants.DOCUMENT_TYPE_RUC);
		assertTrue(validator.validate(yankiRequest).isEmpty());
		existsDocumentNumber(Mono.just(false));
		getCustomer(Mono.empty());
		registerAccount();
		saveYanki();
		yankiService.createYanki(yankiRequest);
	}

	@Test
	public void listCustomer_success1() {
		when(yankiRepository.findAllByIsActiveTrue())
				.thenReturn(Flux.empty());
		assertNotNull(yankiService.findAllYanki());
	}

	@Test
	public void listCustomer_success2() {
		when(yankiRepository.findAllByIsActiveTrue())
				.thenReturn(Flux.fromIterable(Arrays.asList(new YankiEntity())));
		assertNotNull(yankiService.findAllYanki());
	}

	@Test
	public void update_yankiexists() {
		YankiUpdateRequest yankiUpdateRequest = new YankiUpdateRequest();
		yankiUpdateRequest.setAccountNumber("12345678974125");
		assertTrue(validator.validate(yankiUpdateRequest).isEmpty());
		getByPhone(Mono.empty());

		assertThrows(Exception.class, () -> {
			yankiService.updateYanki("987456321", yankiUpdateRequest);
		});
	}

	@Test
	public void update_emptyholder() {
		YankiUpdateRequest yankiUpdateRequest = new YankiUpdateRequest();
		yankiUpdateRequest.setAccountNumber("12345678974125");
		assertTrue(validator.validate(yankiUpdateRequest).isEmpty());
		getByPhone(Mono.just(getYankiEntity()));
		findByHolder(Flux.empty());

		assertThrows(Exception.class, () -> {
			yankiService.updateYanki("987456321", yankiUpdateRequest);
		});
	}

	@Test
	public void update_success() {
		YankiUpdateRequest yankiUpdateRequest = new YankiUpdateRequest();
		yankiUpdateRequest.setAccountNumber("12345678974125");
		assertTrue(validator.validate(yankiUpdateRequest).isEmpty());
		getByPhone(Mono.just(getYankiEntity()));

		findByHolder(Flux.fromIterable(Arrays
				.asList(getAccountEntity("07474589", Arrays.asList("12345678"), null,
						Constants.ACCOUNT_TYPE_CHECKING))));

		saveYanki();
		yankiService.updateYanki("987456321", yankiUpdateRequest);
	}

	@Test
	public void delete_error() {
		getByPhone(Mono.empty());

		assertThrows(Exception.class, () -> {
			yankiService.deleteYanki("987456321");
		});
	}

	@Test
	public void delete_success() {
		getByPhone(Mono.just(getYankiEntity()));
		yankiService.deleteYanki("987456321");
	}

	private YankiRequest getYankiRequest(String document) {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setName("joe doe");
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType(document);
		yankiRequest.setDocumentNumber("12345678");
		return yankiRequest;
	}

	private void registerAccount() {
		when(accountsService
				.registerAccount(any(AccountRequest.class)))
				.thenReturn(new AccountResponse());
	}

	private CustomerEntity getCustomerEntity(String value, String document) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setId(value);
		customerEntity.setFullName("maria vasquez");
		customerEntity.setEmail(value + "@gmail.com");
		customerEntity.setAddress("calle falsa" + value);
		customerEntity.setPhoneNumber(value);
		customerEntity.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerEntity.setDocumentNumber(value);
		customerEntity.setDocumentType(document);
		customerEntity.setIsActive(true);
		return customerEntity;
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

	private void existsDocumentNumber(Mono<Boolean> exists) {
		when(yankiRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void getCustomer(Mono<CustomerEntity> customerEntity) {
		when(customerRepository
				.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(customerEntity);
	}

	private void getByPhone(Mono<YankiEntity> yankiEntity) {
		when(yankiRepository
				.findByPhoneNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(yankiEntity);
	}

	private void findByHolder(Flux<AccountEntity> accountEntity) {
		when(accountRepository.findByHolderDocContainingAndIsActiveTrue(any(String.class)))
				.thenReturn(accountEntity);
	}

	private void saveYanki() {
		when(yankiRepository.save(any(YankiEntity.class))).thenAnswer(invocation -> {
			YankiEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
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
