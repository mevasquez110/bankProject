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
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.request.CreditRequest;
import com.nttdata.bank.response.CreditResponse;
import com.nttdata.bank.response.CreditDebtResponse;

@SpringBootTest
public class CreditControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditService creditService;

	@InjectMocks
	private CreditControllerTest creditControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
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
	@Order(2)
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
	@Order(3)
	public void testValidCreditRequest() {
		CreditRequest creditRequest = new CreditRequest();
		creditRequest.setDocumentNumber("12345678");
		creditRequest.setAmount(5000.0);
		creditRequest.setAccountNumber("123456789");
		creditRequest.setAnnualInterestRate(0.12);
		creditRequest.setNumberOfInstallments(12);
		creditRequest.setPaymentDay(15);
		creditRequest.setAnnualLateInterestRate(0.15);
		assertTrue(validator.validate(creditRequest).isEmpty());

		CreditResponse creditResponse = new CreditResponse();
		Mockito.when(creditService.grantCredit(creditRequest)).thenReturn(creditResponse);

		CreditResponse result = creditService.grantCredit(creditRequest);
		assertNotNull(result);
		Mockito.verify(creditService).grantCredit(creditRequest);
	}

	@Test
	@Order(4)
	public void testCheckDebtCredit() {
		String creditId = "CREDIT123";
		CreditDebtResponse creditDebtResponse = new CreditDebtResponse();
		Mockito.when(creditService.checkDebtCredit(creditId)).thenReturn(creditDebtResponse);

		CreditDebtResponse result = creditService.checkDebtCredit(creditId);
		assertNotNull(result);
		Mockito.verify(creditService).checkDebtCredit(creditId);
	}

	@Test
	@Order(5)
	public void testFindAllCredits() {
		List<CreditResponse> creditList = Arrays.asList(new CreditResponse(), new CreditResponse());
		Mockito.when(creditService.findAllCredits()).thenReturn(creditList);

		List<CreditResponse> result = creditService.findAllCredits();
		assertNotNull(result);
		Mockito.verify(creditService).findAllCredits();
	}

	@Test
	@Order(6)
	public void testDesactivateCredit() {
		String creditId = "CREDIT123";
		creditService.desactivateCredit(creditId);
		Mockito.verify(creditService).desactivateCredit(creditId);
	}
}
