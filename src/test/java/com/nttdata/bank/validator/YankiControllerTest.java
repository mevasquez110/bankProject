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
import com.nttdata.bank.service.YankiService;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.request.YankiUpdateRequest;
import com.nttdata.bank.response.YankiResponse;

@SpringBootTest
public class YankiControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private YankiService yankiService;

	@InjectMocks
	private YankiControllerTest yankiControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidYankiRequestWithMissingName() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType("DNI");
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}

	@Test
	@Order(2)
	public void testInvalidYankiRequestWithInvalidPhoneNumber() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setName("John Doe");
		yankiRequest.setPhoneNumber("12345abc");
		yankiRequest.setDocumentType("DNI");
		yankiRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(yankiRequest).isEmpty());
	}

	@Test
	@Order(3)
	public void testValidYankiRequest() {
		YankiRequest yankiRequest = new YankiRequest();
		yankiRequest.setName("John Doe");
		yankiRequest.setPhoneNumber("987654321");
		yankiRequest.setDocumentType("DNI");
		yankiRequest.setDocumentNumber("12345678");
		assertTrue(validator.validate(yankiRequest).isEmpty());

		YankiResponse yankiResponse = new YankiResponse();
		Mockito.when(yankiService.createYanki(yankiRequest)).thenReturn(yankiResponse);

		YankiResponse result = yankiService.createYanki(yankiRequest);
		assertNotNull(result);
		Mockito.verify(yankiService).createYanki(yankiRequest);
	}

	@Test
	@Order(4)
	public void testFindAllYanki() {
		List<YankiResponse> yankiList = Arrays.asList(new YankiResponse(), new YankiResponse());
		Mockito.when(yankiService.findAllYanki()).thenReturn(yankiList);

		List<YankiResponse> result = yankiService.findAllYanki();
		assertNotNull(result);
		Mockito.verify(yankiService).findAllYanki();
	}

	@Test
	@Order(5)
	public void testUpdateYanki() {
		String phoneNumber = "987654321";
		YankiUpdateRequest yankiUpdateRequest = new YankiUpdateRequest();
		yankiUpdateRequest.setAccountNumber("123456789");

		YankiResponse yankiResponse = new YankiResponse();
		Mockito.when(yankiService.updateYanki(phoneNumber, yankiUpdateRequest)).thenReturn(yankiResponse);

		YankiResponse result = yankiService.updateYanki(phoneNumber, yankiUpdateRequest);
		assertNotNull(result);
		Mockito.verify(yankiService).updateYanki(phoneNumber, yankiUpdateRequest);
	}

	@Test
	@Order(6)
	public void testDeleteYanki() {
		String phoneNumber = "987654321";
		yankiService.deleteYanki(phoneNumber);
		Mockito.verify(yankiService).deleteYanki(phoneNumber);
	}
}
