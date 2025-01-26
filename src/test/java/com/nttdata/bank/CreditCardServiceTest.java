package com.nttdata.bank;

import static org.testng.Assert.assertFalse;
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
import com.nttdata.bank.service.CreditCardService;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.request.ConsumptionRequest;

@SpringBootTest
public class CreditCardServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditCardService creditCardService;

	@InjectMocks
	private CreditCardServiceTest creditCardServiceTest;

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
}
