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
import com.nttdata.bank.service.YankiService;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.request.YankiRequest;

@SpringBootTest
public class YankiServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private YankiService yankiService;

	@InjectMocks
	private YankiServiceTest yankiServiceTest;

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
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType("INVALID_DOCUMENT_TYPE");
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}
}
