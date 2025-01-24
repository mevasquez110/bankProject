package com.nttdata.bank.validator;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.nttdata.bank.service.CreditCardService;
import com.nttdata.bank.request.CreditCardRequest;
import com.nttdata.bank.request.ConsumptionRequest;
import com.nttdata.bank.response.CreditCardResponse;
import com.nttdata.bank.response.CreditCardDebtResponse;
import com.nttdata.bank.response.ConsumptionResponse;

@SpringBootTest
public class CreditCardControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditCardService creditCardService;

	@InjectMocks
	private CreditCardControllerTest creditCardControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidCreditCardRequestWithMissingDocumentNumber() {
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setAvailableCredit(1000.0);
		creditCardRequest.setAnnualInterestRate(0.15);
		creditCardRequest.setPaymentDay(15);
		creditCardRequest.setAnnualLateInterestRate(0.25);
		assertFalse(validator.validate(creditCardRequest).isEmpty());
	}

	@Test
	@Order(2)
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
	@Order(3)
	public void testValidCreditCardRequest() {
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setDocumentNumber("12345678");
		creditCardRequest.setAvailableCredit(1000.0);
		creditCardRequest.setAnnualInterestRate(0.15);
		creditCardRequest.setPaymentDay(15);
		creditCardRequest.setAnnualLateInterestRate(0.25);
		assertTrue(validator.validate(creditCardRequest).isEmpty());

		CreditCardResponse creditCardResponse = new CreditCardResponse();
		Mockito.when(creditCardService.requestCreditCard(creditCardRequest)).thenReturn(creditCardResponse);

		CreditCardResponse result = creditCardService.requestCreditCard(creditCardRequest);
		assertNotNull(result);
		Mockito.verify(creditCardService).requestCreditCard(creditCardRequest);
	}

	@Test
	@Order(4)
	public void testCheckDebtCreditCard() {
		String creditCardNumber = "123456789";
		CreditCardDebtResponse creditCardDebtResponse = new CreditCardDebtResponse();
		Mockito.when(creditCardService.checkDebtCreditCard(creditCardNumber)).thenReturn(creditCardDebtResponse);

		CreditCardDebtResponse result = creditCardService.checkDebtCreditCard(creditCardNumber);
		assertNotNull(result);
		Mockito.verify(creditCardService).checkDebtCreditCard(creditCardNumber);
	}

	@Test
	@Order(5)
	public void testFindAllCreditCards() {
		List<CreditCardResponse> creditCardList = Arrays.asList(new CreditCardResponse(), new CreditCardResponse());
		Mockito.when(creditCardService.findAllCreditCards()).thenReturn(creditCardList);

		List<CreditCardResponse> result = creditCardService.findAllCreditCards();
		assertNotNull(result);
		Mockito.verify(creditCardService).findAllCreditCards();
	}

	@Test
	@Order(6)
	public void testUpdateCreditCard() {
		String creditCardNumber = "123456789";
		CreditCardResponse creditCardResponse = new CreditCardResponse();
		Mockito.when(creditCardService.updateCreditCard(creditCardNumber)).thenReturn(creditCardResponse);

		CreditCardResponse result = creditCardService.updateCreditCard(creditCardNumber);
		assertNotNull(result);
		Mockito.verify(creditCardService).updateCreditCard(creditCardNumber);
	}

	@Test
	@Order(7)
	public void testDeleteCreditCard() {
		String creditCardNumber = "123456789";
		creditCardService.deleteCreditCard(creditCardNumber);
		Mockito.verify(creditCardService).deleteCreditCard(creditCardNumber);
	}

	@Test
	@Order(8)
	public void testValidConsumptionRequest() {
		ConsumptionRequest consumptionRequest = new ConsumptionRequest();
		consumptionRequest.setCreditCardNumber("123456789");
		consumptionRequest.setAmount(100.0);
		consumptionRequest.setNumberOfInstallments(12);
		consumptionRequest.setProductOrServiceName("Laptop");

		assertTrue(validator.validate(consumptionRequest).isEmpty());

		ConsumptionResponse consumptionResponse = new ConsumptionResponse();
		Mockito.when(creditCardService.chargeConsumption(consumptionRequest)).thenReturn(consumptionResponse);

		ConsumptionResponse result = creditCardService.chargeConsumption(consumptionRequest);
		assertNotNull(result);
		Mockito.verify(creditCardService).chargeConsumption(consumptionRequest);
	}

	@Test
	@Order(9)
	public void testInvalidConsumptionRequestWithMissingCreditCardNumber() {
		ConsumptionRequest consumptionRequest = new ConsumptionRequest();
		consumptionRequest.setAmount(100.0);
		consumptionRequest.setNumberOfInstallments(12);
		consumptionRequest.setProductOrServiceName("Laptop");

		assertFalse(validator.validate(consumptionRequest).isEmpty());
	}
}
