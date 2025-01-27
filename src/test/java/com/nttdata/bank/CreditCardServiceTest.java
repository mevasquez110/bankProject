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
import com.nttdata.bank.service.impl.CreditCardServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.entity.Consumption;
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.CreditCardScheduleEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.repository.CreditCardRepository;
import com.nttdata.bank.repository.CreditCardScheduleRepository;
import com.nttdata.bank.repository.CreditRepository;
import com.nttdata.bank.repository.CreditScheduleRepository;
import com.nttdata.bank.request.ConsumptionRequest;

@SpringBootTest
public class CreditCardServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditCardRepository creditCardRepository;

	@Mock
	private CreditRepository creditRepository;

	@Mock
	private CreditScheduleRepository creditScheduleRepository;

	@Mock
	private CreditCardScheduleRepository creditCardScheduleRepository;

	@InjectMocks
	private CreditCardServiceImpl creditCardService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidCreditCardRequestWithMissingDocumentNumber() {
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setAvailableCredit(1000.0);
		creditCardRequest.setAnnualInterestRate(0.15);
		creditCardRequest.setPaymentDay(15);
		creditCardRequest.setAnnualLateInterestRate(0.25);
		assertFalse(validator.validate(creditCardRequest).isEmpty());
	}

	@Test
	public void testInvalidCreditCardRequestWithNegativeAvailableCredit() {
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setDocumentNumber("12345678");
		creditCardRequest.setAvailableCredit(-1000.0);
		creditCardRequest.setAnnualInterestRate(0.15);
		creditCardRequest.setPaymentDay(15);
		creditCardRequest.setAnnualLateInterestRate(0.25);
		assertFalse(validator.validate(creditCardRequest).isEmpty());
	}

	@Test
	public void testInvalidConsumptionRequestWithMissingCreditCardNumber() {
		ConsumptionRequest consumptionRequest = new ConsumptionRequest();
		consumptionRequest.setAmount(100.0);
		consumptionRequest.setNumberOfInstallments(12);
		consumptionRequest.setProductOrServiceName("Laptop");
		assertFalse(validator.validate(consumptionRequest).isEmpty());
	}

	@Test
	public void testInvalidConsumptionRequestSizeCreditCard() {
		ConsumptionRequest consumptionRequest = new ConsumptionRequest();
		consumptionRequest.setCreditCardNumber("123");
		consumptionRequest.setAmount(100.0);
		consumptionRequest.setNumberOfInstallments(12);
		consumptionRequest.setProductOrServiceName("Laptop");
		assertFalse(validator.validate(consumptionRequest).isEmpty());
	}

	@Test
	public void create_CreditCardExists() {
		CreditCardRequest creditCardRequest = getCreditCardRequest();
		assertTrue(validator.validate(creditCardRequest).isEmpty());
		existsCreditCard(Mono.just(true));

		assertThrows(Exception.class, () -> {
			creditCardService.requestCreditCard(creditCardRequest);
		});
	}

	@Test
	public void create_hasADebt() {
		CreditCardRequest creditCardRequest = getCreditCardRequest();
		assertTrue(validator.validate(creditCardRequest).isEmpty());
		existsCreditCard(Mono.just(false));
		getCredit(Mono.just(getCreditEntity()));

		getListCreditScheduleLess(Flux.fromIterable(Arrays
				.asList(geCreditScheduleEntity())));

		assertThrows(Exception.class, () -> {
			creditCardService.requestCreditCard(creditCardRequest);
		});
	}

	@Test
	public void create_Success() {
		CreditCardRequest creditCardRequest = getCreditCardRequest();
		assertTrue(validator.validate(creditCardRequest).isEmpty());
		existsCreditCard(Mono.just(false));
		saveCreditCard();
		getCredit(Mono.just(getCreditEntity()));
		getListCreditScheduleLess(Flux.empty());
		existsCreditCardNumber(Mono.just(false));
		creditCardService.requestCreditCard(creditCardRequest);
	}

	@Test
	public void checkDebt_CreditCardExists() {
		getCreditCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			creditCardService.checkDebtCreditCard("123");
		});
	}

	@Test
	public void checkDebt_Sucess() {
		getCreditCard(Mono.just(getCreditCardEntity(true)));
		getScheduleDateLess();

		getScheduleDateAfter(Flux.fromIterable(Arrays.asList(getCreditCardScheduleEntity(),
				getCreditCardScheduleEntity())));

		creditCardService.checkDebtCreditCard("123");
	}

	@Test
	public void update_CreditCardExists() {
		getCreditCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			creditCardService.updateCreditCard("123");
		});
	}

	@Test
	public void update_Success1() {
		getCreditCard(Mono.just(getCreditCardEntity(true)));
		saveCreditCard();
		creditCardService.updateCreditCard("123");
	}

	@Test
	public void update_Success2() {
		getCreditCard(Mono.just(getCreditCardEntity(false)));
		saveCreditCard();
		creditCardService.updateCreditCard("123");
	}

	@Test
	public void delete_CreditCardEmpty() {
		getCreditCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			creditCardService.deleteCreditCard("123");
		});
	}

	@Test
	public void delete_cardDebt() {
		getCreditCard(Mono.just(getCreditCardEntity(false)));
		getScheduleDateLess();

		getScheduleDateAfter(Flux.fromIterable(Arrays.asList(getCreditCardScheduleEntity(),
				getCreditCardScheduleEntity())));

		assertThrows(Exception.class, () -> {
			creditCardService.deleteCreditCard("123");
		});
	}

	@Test
	public void delete_Success() {
		getCreditCard(Mono.just(getCreditCardEntity(false)));
		getListCreditCardScheduleLess(Flux.empty());
		getScheduleDateAfter(Flux.empty());
		saveCreditCard();
		creditCardService.deleteCreditCard("123");
	}

	@Test
	public void listCreditCards_success() {
		when(creditCardRepository.findByIsActiveTrue())
				.thenReturn(Flux.empty());

		creditCardService.findAllCreditCards();
	}

	@Test
	public void charge_allow() {
		ConsumptionRequest consumptionRequest = getConsumptionRequest(200.00);
		assertTrue(validator.validate(consumptionRequest).isEmpty());
		getCreditCard(Mono.just(getCreditCardEntity(false)));

		assertThrows(Exception.class, () -> {
			creditCardService.chargeConsumption(consumptionRequest);
		});
	}

	@Test
	public void charge_amount() {
		ConsumptionRequest consumptionRequest = getConsumptionRequest(50.00);
		assertTrue(validator.validate(consumptionRequest).isEmpty());
		getCreditCard(Mono.just(getCreditCardEntity(true)));

		assertThrows(Exception.class, () -> {
			creditCardService.chargeConsumption(consumptionRequest);
		});
	}

	@Test
	public void charge_NewSchedule_Success() {
		ConsumptionRequest consumptionRequest = getConsumptionRequest(80.00);
		assertTrue(validator.validate(consumptionRequest).isEmpty());
		getCreditCard(Mono.just(getCreditCardEntity(true)));

		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaymentDate(any(String.class),
						any(LocalDate.class)))
				.thenReturn(Mono.empty());

		saveScheduleCreditCard();
		saveCreditCard();
		creditCardService.chargeConsumption(consumptionRequest);
	}

	@Test
	public void charge_PreSchedule_Success() {
		ConsumptionRequest consumptionRequest = getConsumptionRequest(50.00);
		assertTrue(validator.validate(consumptionRequest).isEmpty());
		getCreditCard(Mono.just(getCreditCardEntity(true)));
		getScheduleCreditCard(Mono.just(getCreditCardScheduleEntity()));
		saveScheduleCreditCard();
		saveCreditCard();
		creditCardService.chargeConsumption(consumptionRequest);
	}

	@Test
	public void update_notcrecitcard() {
		getCreditCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			creditCardService.updateBalance("123", 234.00);
		});
	}

	@Test
	public void update_sucess() {
		getCreditCard(Mono.just(getCreditCardEntity(true)));
		saveCreditCard();
		creditCardService.updateBalance("123", 234.00);
	}

	private ConsumptionRequest getConsumptionRequest(Double amount) {
		ConsumptionRequest consumptionRequest = new ConsumptionRequest();
		consumptionRequest.setAmount(amount);
		consumptionRequest.setCreditCardNumber("1234567890123456");
		consumptionRequest.setNumberOfInstallments(10);
		consumptionRequest.setProductOrServiceName("TABLET");
		return consumptionRequest;
	}

	private CreditScheduleEntity geCreditScheduleEntity() {
		CreditScheduleEntity creditScheduleEntity = new CreditScheduleEntity();
		creditScheduleEntity.setId("123");
		return creditScheduleEntity;
	}

	private CreditCardRequest getCreditCardRequest() {
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setAnnualInterestRate(0.12);
		creditCardRequest.setAnnualLateInterestRate(0.18);
		creditCardRequest.setAvailableCredit(1000.00);
		creditCardRequest.setDocumentNumber("123");
		creditCardRequest.setPaymentDay(5);
		return creditCardRequest;
	}

	private void existsCreditCard(Mono<Boolean> exists) {
		when(creditCardRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void getScheduleDateLess() {
		getListCreditCardScheduleLess(Flux.fromIterable(Arrays.asList(getCreditCardScheduleEntity(),
				getCreditCardScheduleEntity())));
	}

	private void getScheduleCreditCard(Mono<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaymentDate(any(String.class),
						any(LocalDate.class)))
				.thenReturn(creditCardScheduleEntity);
	}

	private void getScheduleDateAfter(Flux<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateAfter(any(String.class),
						any(LocalDateTime.class)))
				.thenReturn(creditCardScheduleEntity);
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

	private void getCreditCard(Mono<CreditCardEntity> creditCardEntity) {
		when(creditCardRepository.findByCreditCardNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditCardEntity);
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

	private void getCredit(Mono<CreditEntity> creditEntity) {
		when(creditRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(creditEntity);
	}

	private void saveScheduleCreditCard() {
		when(creditCardScheduleRepository.save(any(CreditCardScheduleEntity.class)))
				.thenAnswer(invocation -> {
					CreditCardScheduleEntity entity = invocation.getArgument(0);
					return Mono.just(entity);
				});
	}

	private void saveCreditCard() {
		when(creditCardRepository.save(any(CreditCardEntity.class))).thenAnswer(invocation -> {
			CreditCardEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

	private void existsCreditCardNumber(Mono<Boolean> exists) {
		when(creditCardRepository.existsByCreditCardNumber(any(String.class)))
				.thenReturn(exists);
	}

	private void getListCreditScheduleLess(Flux<CreditScheduleEntity> creditScheduleEntity) {
		when(creditScheduleRepository.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
				any(String.class), any(LocalDateTime.class)))
				.thenReturn(creditScheduleEntity);
	}

	private void getListCreditCardScheduleLess(
			Flux<CreditCardScheduleEntity> creditCardScheduleEntity) {
		when(creditCardScheduleRepository
				.findByCreditCardNumberAndPaidFalseAndPaymentDateLessThanEqual(
						any(String.class), any(LocalDate.class)))
				.thenReturn(creditCardScheduleEntity);
	}

	private CreditEntity getCreditEntity() {
		CreditEntity creditEntity = new CreditEntity();
		creditEntity.setId("123");
		return creditEntity;
	}

}
