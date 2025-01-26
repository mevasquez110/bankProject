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
import com.nttdata.bank.service.DebitCardService;
import com.nttdata.bank.request.DebitCardRequest;

@SpringBootTest
public class DebitCardServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private DebitCardService debitCardService;

	@InjectMocks
	private DebitCardServiceTest debitCardServiceTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidDebitCardRequestWithMissingDocumentNumber() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setPrimaryAccount("123456789");
		assertFalse(validator.validate(debitCardRequest).isEmpty());
	}

	@Test
	public void testInvalidDebitCardRequestWithMissingPrimaryAccount() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(debitCardRequest).isEmpty());
	}

}
