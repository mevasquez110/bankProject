package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import com.nttdata.bank.entity.CreditCardEntity;
import com.nttdata.bank.entity.CreditEntity;
import com.nttdata.bank.entity.CreditScheduleEntity;
import com.nttdata.bank.repository.CreditCardRepository;
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
		existsCreditCard(true);

		assertThrows(Exception.class, () -> {
			creditCardService.requestCreditCard(creditCardRequest);
		});
	}

	@Test
	public void create_hasADebt() {
		CreditCardRequest creditCardRequest = getCreditCardRequest();

		assertTrue(validator.validate(creditCardRequest).isEmpty());
		existsCreditCard(false);
		getCredit();

		when(creditScheduleRepository.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
				any(String.class), any(LocalDateTime.class)))
				.thenReturn(Flux.fromIterable(Arrays
						.asList(geCreditScheduleEntity())));

		assertThrows(Exception.class, () -> {
			creditCardService.requestCreditCard(creditCardRequest);
		});
	}

	@Test
	public void create_Success() {
		CreditCardRequest creditCardRequest = getCreditCardRequest();
		assertTrue(validator.validate(creditCardRequest).isEmpty());
		existsCreditCard(false);
		saveCreditCard();
		getCredit();

		when(creditScheduleRepository.findByCreditIdAndPaidFalseAndPaymentDateLessThanEqual(
				any(String.class), any(LocalDateTime.class)))
				.thenReturn(Flux.empty());

		existsCreditCardNumber();
		creditCardService.requestCreditCard(creditCardRequest);
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

	private void existsCreditCard(Boolean exists) {
		when(creditCardRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(exists));
	}

	private void getCredit() {
		CreditEntity creditEntity = new CreditEntity();
		creditEntity.setId("123");

		when(creditRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(Mono.just(creditEntity));
	}

	private void saveCreditCard() {
		when(creditCardRepository.save(any(CreditCardEntity.class))).thenAnswer(invocation -> {
			CreditCardEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

	private void existsCreditCardNumber() {
		when(creditCardRepository.existsByCreditCardNumber(any(String.class)))
				.thenReturn(Mono.just(false));
	}

}
