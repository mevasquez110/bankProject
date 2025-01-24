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
import com.nttdata.bank.request.ContactDataRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.response.CustomerResponse;
import com.nttdata.bank.service.CustomerService;
import com.nttdata.bank.util.Constants;

@SpringBootTest
public class CustomerControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private CustomerControllerTest customerControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidPersonalCustomerWithInvalidDocumentType() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType("INVALID_DOC_TYPE");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(2)
	public void testInvalidPersonalCustomerWithShortDni() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setEmail("email");
		customerRequest.setPhoneNumber("989");
		customerRequest.setDocumentNumber("123");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(3)
	public void testInvalidPersonalCustomerWithNullFullName() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(4)
	public void testInvalidPersonalCustomerWithCompanyNameNotNull() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setDocumentNumber("12345678");
		customerRequest.setFullName("Andres Fernandez");
		customerRequest.setCompanyName("Company Name");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(5)
	public void testInvalidBusinessCustomerWithMissingCompanyName() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("12345678901");
		customerRequest.setFullName(null);
		customerRequest.setCompanyName(null);
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(6)
	public void testInvalidBusinessCustomerWithFullNameNotNull() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("12345678901");
		customerRequest.setFullName("Cristopher Doe");
		customerRequest.setCompanyName("Acme Corp");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(7)
	public void testInvalidBusinessCustomerWithShortDocumentNumber() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("123456");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(8)
	public void testInvalidBusinessCustomerWithWrongDocumentType() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setDocumentNumber("12345678901");
		customerRequest.setFullName(null);
		customerRequest.setCompanyName("Acme Corp");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	@Order(9)
	public void testValidPersonalCustomer() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setDocumentNumber("12345678");
		customerRequest.setFullName("Rocio Kamell");
		customerRequest.setAddress("Av. Siempre Viva 456");
		customerRequest.setEmail("rocio.kamell@example.com");
		customerRequest.setPhoneNumber("987351648");

		assertTrue(validator.validate(customerRequest).isEmpty());

		CustomerResponse customerResponse = new CustomerResponse();
		Mockito.when(customerService.createCustomer(customerRequest)).thenReturn(customerResponse);

		CustomerResponse result = customerService.createCustomer(customerRequest);
		assertNotNull(result);
		Mockito.verify(customerService).createCustomer(customerRequest);
	}

	@Test
	@Order(10)
	public void testValidBusinessCustomer() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("12345678901");
		customerRequest.setFullName(null);
		customerRequest.setCompanyName("Acme Corp");
		customerRequest.setAddress("Av. Alameda Sur 456");
		customerRequest.setEmail("Acme@corp.com");
		customerRequest.setPhoneNumber("985214785");

		assertTrue(validator.validate(customerRequest).isEmpty());

		CustomerResponse customerResponse = new CustomerResponse();
		Mockito.when(customerService.createCustomer(customerRequest)).thenReturn(customerResponse);

		CustomerResponse result = customerService.createCustomer(customerRequest);
		assertNotNull(result);
		Mockito.verify(customerService).createCustomer(customerRequest);
	}

	@Test
	@Order(11)
	public void testGetCustomerByDocumentNumber() {
		String documentNumber = "12345678";
		CustomerResponse customerResponse = new CustomerResponse();
		Mockito.when(customerService.getCustomerByDocumentNumber(documentNumber)).thenReturn(customerResponse);

		CustomerResponse result = customerService.getCustomerByDocumentNumber(documentNumber);
		assertNotNull(result);
		Mockito.verify(customerService).getCustomerByDocumentNumber(documentNumber);
	}

	@Test
	@Order(12)
	public void testFindAllCustomers() {
		List<CustomerResponse> customerList = Arrays.asList(new CustomerResponse(), new CustomerResponse());
		Mockito.when(customerService.findAllCustomers()).thenReturn(customerList);
		List<CustomerResponse> result = customerService.findAllCustomers();
		assertNotNull(result);
		Mockito.verify(customerService).findAllCustomers();
	}

	@Test
	@Order(13)
	public void testUpdateCustomer() {
		String documentNumber = "12345678";
		ContactDataRequest contactDataRequest = new ContactDataRequest();
		contactDataRequest.setEmail("updated.email@example.com");
		contactDataRequest.setAddress("Updated Address");
		contactDataRequest.setPhoneNumber("987654321");

		CustomerResponse customerResponse = new CustomerResponse();
		Mockito.when(customerService.updateCustomer(documentNumber, contactDataRequest)).thenReturn(customerResponse);

		CustomerResponse result = customerService.updateCustomer(documentNumber, contactDataRequest);
		assertNotNull(result);
		Mockito.verify(customerService).updateCustomer(documentNumber, contactDataRequest);
	}

	@Test
	@Order(14)
	public void testUpdateCustomerWithInvalidRequest() {
		ContactDataRequest contactDataRequest = new ContactDataRequest();
		contactDataRequest.setEmail("invalid-email");
		contactDataRequest.setAddress("");
		contactDataRequest.setPhoneNumber("123");
		assertFalse(validator.validate(contactDataRequest).isEmpty());
	}

	@Test
	@Order(15)
	public void testDeleteCustomer() {
		String documentNumber = "12345678";
		customerService.deleteCustomer(documentNumber);
		Mockito.verify(customerService).deleteCustomer(documentNumber);
	}

}
