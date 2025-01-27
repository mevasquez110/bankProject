package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.service.impl.CreditServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.nttdata.bank.entity.Consumption;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditCardScheduleRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CreditScheduleRepository;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.TransactionResponse;

@SpringBootTest
public class CreditServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditCardRepository creditCardRepository;

	@Mock
	private CreditRepository creditRepository;

	@Mock
	private CreditScheduleRepository creditScheduleRepository;

	@Mock
	private OperationService operationService;

	@Mock
	private AccountsService accountService;

	@Mock
	private CreditCardScheduleRepository creditCardScheduleRepository;

	@InjectMocks
	private CreditServiceImpl creditService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidCreditRequestWithMissingDocumentNumber() {
		CreditRequest creditRequest = new CreditRequest();
		creditRequest.setAmount(5000.0);
		creditRequest.setAccountNumber("123456789");
		creditRequest.setAnnualInterestRate(0.12);
		creditRequest.setNumberOfInstallments(12);
		creditRequest.setPaymentDay(15);
		creditRequest.setAnnualLateInterestRate(0.15);
		assertFalse(validator.validate(creditRequest).isEmpty());
	}

	@Test
	public void testInvalidCreditRequestWithNegativeAmount() {
		CreditRequest creditRequest = new CreditRequest();
		creditRequest.setDocumentNumber("12345678");
		creditRequest.setAmount(-5000.0);
		creditRequest.setAccountNumber("123456789");
		creditRequest.setAnnualInterestRate(0.12);
		creditRequest.setNumberOfInstallments(12);
		creditRequest.setPaymentDay(15);
		creditRequest.setAnnualLateInterestRate(0.15);
		assertFalse(validator.validate(creditRequest).isEmpty());
	}

	@Test
	public void create_CreditExists() {
		CreditRequest creditRequest = getCreditRequest();
		assertTrue(validator.validate(creditRequest).isEmpty());
		existsCreditByDocument(Mono.just(true));

		assertThrows(Exception.class, () -> {
			creditService.grantCredit(creditRequest);
		});
	}

	@Test
	public void create_hasdebt() {
		CreditRequest creditRequest = getCreditRequest();
		assertTrue(validator.validate(creditRequest).isEmpty());
		existsCreditByDocument(Mono.just(false));
		getCreditCardByDocument(Mono.just(getCreditCardEntity(true)));

		getListCreditCardScheduleLess(Flux.fromIterable(Arrays.asList(getCreditCardScheduleEntity(),
				getCreditCardScheduleEntity())));

		assertThrows(Exception.class, () -> {
			creditService.grantCredit(creditRequest);
		});
	}

	@Test
	public void create_Success() {
		CreditRequest creditRequest = getCreditRequest();
		assertTrue(validator.validate(creditRequest).isEmpty());
		existsCreditByDocument(Mono.just(false));
		getCreditCardByDocument(Mono.empty());
		saveCredit();
		makeDeposit();
		updateAccount();
		saveAllSchedule();
		creditService.grantCredit(creditRequest);
	}

	@Test
	public void checkDebt_CreditExists() {
		CreditRequest creditRequest = getCreditRequest();
		assertTrue(validator.validate(creditRequest).isEmpty());
		existsCreditId(Mono.just(false));

		assertThrows(Exception.class, () -> {
			creditService.checkDebtCredit("123");
		});
	}

	@Test
	public void checkDebt_Success() {
		CreditRequest creditRequest = getCreditRequest();
		assertTrue(validator.validate(creditRequest).isEmpty());
		existsCreditId(Mono.just(true));

		getListCreditScheduleLess(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		getScheduleDateAfter(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		creditService.checkDebtCredit("123");
	}

	@Test
	public void listCredit_success() {
		when(creditRepository.findByIsActiveTrue())
				.thenReturn(Flux.empty());

		creditService.findAllCredits();
	}

	@Test
	public void delete_CreditExists() {
		existsCreditId(Mono.just(false));

		assertThrows(Exception.class, () -> {
			creditService.desactivateCredit("123");
		});
	}

	@Test
	public void delete_success() {
		getCreditId(Mono.just(new CreditEntity()));
		saveCredit();
		creditService.desactivateCredit("123");
	}

	private CreditScheduleEntity geCreditScheduleEntity() {
		CreditScheduleEntity creditScheduleEntity = new CreditScheduleEntity();
		creditScheduleEntity.setId("123");
		creditScheduleEntity.setCurrentDebt(100.00);
		return creditScheduleEntity;
	}

	private void getListCreditScheduleLess(Flux<CreditScheduleEntity> creditScheduleEntity) {
		when(creditScheduleRepository.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
				any(String.class), any(LocalDateTime.class)))
				.thenReturn(creditScheduleEntity);
	}

	private void getScheduleDateAfter(Flux<CreditScheduleEntity> creditScheduleEntity) {
		when(creditScheduleRepository
				.findByCreditIdAndPaidFalseAndPaymentDateAfter(any(String.class),
						any(LocalDateTime.class)))
				.thenReturn(creditScheduleEntity);
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

	private void saveCredit() {
		when(creditRepository.save(any(CreditEntity.class))).thenAnswer(invocation -> {
			CreditEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

	@SuppressWarnings("unchecked")
	private void saveAllSchedule() {
		when(creditScheduleRepository.saveAll(any(Flux.class))).thenAnswer(invocation -> {
			return Flux.empty();
		});
	}

	private void getListCreditCardScheduleLess(
			Flux<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
						any(String.class), any(LocalDate.class)))
				.thenReturn(creditCardScheduleEntity);
	}

	private void getCreditCardByDocument(Mono<CreditCardEntity> creditCardEntity) {
		when(creditCardRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditCardEntity);
	}

	private void existsCreditByDocument(Mono<Boolean> exists) {
		when(creditRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void existsCreditId(Mono<Boolean> exists) {
		when(creditRepository.existsByIdAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void getCreditId(Mono<CreditEntity> creditEntity) {
		when(creditRepository.findByIdAndIsActiveTrue(any(String.class)))
				.thenReturn(creditEntity);
	}

	private CreditRequest getCreditRequest() {
		CreditRequest creditRequest = new CreditRequest();
		creditRequest.setDocumentNumber("12345678");
		creditRequest.setAmount(5000.0);
		creditRequest.setAccountNumber("123456789");
		creditRequest.setAnnualInterestRate(0.12);
		creditRequest.setNumberOfInstallments(12);
		creditRequest.setPaymentDay(15);
		creditRequest.setAnnualLateInterestRate(0.15);
		return creditRequest;
	}

	private CreditCardScheduleEntity getCreditCardScheduleEntity() {
		Random random = new Random();
		CreditCardScheduleEntity creditCardScheduleEntity = new CreditCardScheduleEntity();
		creditCardScheduleEntity.setCurrentDebt(100 + (500 * random.nextDouble()));
		List<Consumption> listConsumption = new ArrayList<>();
		listConsumption.add(getConsumption());
		listConsumption.add(getConsumption());
		creditCardScheduleEntity.setConsumptionQuota(listConsumption);
		return creditCardScheduleEntity;
	}

	private Consumption getConsumption() {
		Random random = new Random();
		Consumption consumption = new Consumption();
		consumption.setAmount(100 + (500 * random.nextDouble()));
		return consumption;
	}

	private void makeDeposit() {
		when(operationService.makeDeposit(any(DepositRequest.class)))
				.thenReturn(Mono.just(new TransactionResponse()));
	}

	private void updateAccount() {
		when(accountService.updateAccount(any(UpdateAccountRequest.class)))
				.thenReturn(new AccountResponse());
	}

}
