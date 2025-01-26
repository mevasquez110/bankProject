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
import com.nttdata.bank.service.CreditService;
import com.nttdata.bank.request.CreditRequest;

@SpringBootTest
public class CreditServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CreditService creditService;

	@InjectMocks
	private CreditServiceTest creditServiceTest;

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

}
