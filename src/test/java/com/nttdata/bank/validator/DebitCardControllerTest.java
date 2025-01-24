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
import com.nttdata.bank.service.DebitCardService;
import com.nttdata.bank.request.DebitCardRequest;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.response.DebitCardResponse;

@SpringBootTest
public class DebitCardControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private DebitCardService debitCardService;

	@InjectMocks
	private DebitCardControllerTest debitCardControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidDebitCardRequestWithMissingDocumentNumber() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setPrimaryAccount("123456789");
		assertFalse(validator.validate(debitCardRequest).isEmpty());
	}

	@Test
	@Order(2)
	public void testInvalidDebitCardRequestWithMissingPrimaryAccount() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(debitCardRequest).isEmpty());
	}

	@Test
	@Order(3)
	public void testValidDebitCardRequest() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setDocumentNumber("12345678");
		debitCardRequest.setPrimaryAccount("123456789");
		assertTrue(validator.validate(debitCardRequest).isEmpty());

		DebitCardResponse debitCardResponse = new DebitCardResponse();
		Mockito.when(debitCardService.createDebitCard(debitCardRequest)).thenReturn(debitCardResponse);

		DebitCardResponse result = debitCardService.createDebitCard(debitCardRequest);
		assertNotNull(result);
		Mockito.verify(debitCardService).createDebitCard(debitCardRequest);
	}

	@Test
	@Order(4)
	public void testFindAllDebitCards() {
		List<DebitCardResponse> debitCardList = Arrays.asList(new DebitCardResponse(), new DebitCardResponse());
		Mockito.when(debitCardService.findAllDebitCard()).thenReturn(debitCardList);

		List<DebitCardResponse> result = debitCardService.findAllDebitCard();
		assertNotNull(result);
		Mockito.verify(debitCardService).findAllDebitCard();
	}

	@Test
	@Order(5)
	public void testAssociateAccount() {
		String debitCardNumber = "123456789";
		AssociateAccountRequest associateAccountRequest = new AssociateAccountRequest();
		associateAccountRequest.setPrimaryAccount("987654321");
		associateAccountRequest.setAssociatedAccounts(Arrays.asList("123456789", "987654321"));

		DebitCardResponse debitCardResponse = new DebitCardResponse();
		Mockito.when(debitCardService.associateAccount(debitCardNumber, associateAccountRequest))
				.thenReturn(debitCardResponse);

		DebitCardResponse result = debitCardService.associateAccount(debitCardNumber, associateAccountRequest);
		assertNotNull(result);
		Mockito.verify(debitCardService).associateAccount(debitCardNumber, associateAccountRequest);
	}

	@Test
	@Order(6)
	public void testDeleteDebitCard() {
		String debitCardNumber = "123456789";
		debitCardService.deleteDebitCard(debitCardNumber);
		Mockito.verify(debitCardService).deleteDebitCard(debitCardNumber);
	}
}
