package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import com.nttdata.bank.entity.CustomerEntity;
import com.nttdata.bank.repository.CustomerRepository;
import com.nttdata.bank.request.ContactDataRequest;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.service.impl.CustomerServiceImpl;
import com.nttdata.bank.util.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
public class CustomerServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerServiceImpl customerService;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidPersonalCustomerWithInvalidDocumentType() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType("INVALID_DOC_TYPE");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
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
	public void testInvalidPersonalCustomerWithNullFullName() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
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
	public void testInvalidBusinessCustomerWithShortDocumentNumber() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("123456");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
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
	public void testUpdateCustomerWithInvalidRequest() {
		ContactDataRequest contactDataRequest = new ContactDataRequest();
		contactDataRequest.setEmail("invalid-email");
		contactDataRequest.setAddress("");
		contactDataRequest.setPhoneNumber("123");
		assertFalse(validator.validate(contactDataRequest).isEmpty());
	}

	@Test
	public void testInvalidPersonalType() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType("INVALID_PERSON_TYPE");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	public void testInvalidDocument() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_CE);
		customerRequest.setDocumentNumber("123456789741258963214569");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	public void testInvalid() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_CE);
		customerRequest.setDocumentNumber("123456789");
		customerRequest.setFullName("");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	public void testInvalidCompany() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setPersonType(Constants.PERSON_TYPE_BUSINESS);
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_RUC);
		customerRequest.setDocumentNumber("123456789");
		customerRequest.setCompanyName("");
		assertFalse(validator.validate(customerRequest).isEmpty());
	}

	@Test
	public void createCustomer_documentNumberExists() {
		CustomerRequest customerRequest = getCustomerRequest();
		assertTrue(validator.validate(customerRequest).isEmpty());
		existsPhone(Mono.just(false));
		existsDocument(Mono.just(true));

		assertThrows(Exception.class, () -> {
			customerService.createCustomer(customerRequest);
		});
	}

	@Test
	public void createCustomer_phoneNumberExists() {
		CustomerRequest customerRequest = getCustomerRequest();
		assertTrue(validator.validate(customerRequest).isEmpty());
		existsPhone(Mono.just(true));
		existsDocument(Mono.just(false));

		assertThrows(Exception.class, () -> {
			customerService.createCustomer(customerRequest);
		});
	}

	@Test
	public void createCustomer_documentNumberEmpty() {
		CustomerRequest customerRequest = getCustomerRequest();
		assertTrue(validator.validate(customerRequest).isEmpty());
		existsPhone(Mono.just(false));
		existsDocument(Mono.empty());

		assertThrows(Exception.class, () -> {
			customerService.createCustomer(customerRequest);
		});
	}

	@Test
	public void createCustomer_phoneNumberEmpty() {
		CustomerRequest customerRequest = getCustomerRequest();
		assertTrue(validator.validate(customerRequest).isEmpty());
		existsPhone(Mono.empty());
		existsDocument(Mono.just(false));

		assertThrows(Exception.class, () -> {
			customerService.createCustomer(customerRequest);
		});
	}

	@Test
	public void createCustomer_Success() {
		existsPhone(Mono.just(false));
		existsDocument(Mono.just(false));
		saveCustomer();
		customerService.createCustomer(getCustomerRequest());
	}

	@Test
	public void getCustomer_notFound() {
		getCustomer(Mono.empty());

		assertThrows(Exception.class, () -> {
			customerService.getCustomerByDocumentNumber("123456789");
		});
	}

	@Test
	public void getCustomer_suuccess() {
		String documentNumber = "123456789";
		getCustomer(Mono.just(getCustomerEntity(documentNumber)));
		customerService.getCustomerByDocumentNumber(documentNumber);
	}

	@Test
	public void deleteCustomer_notFound() {
		getCustomer(Mono.empty());

		assertThrows(Exception.class, () -> {
			customerService.deleteCustomer("123456789");
		});
	}

	@Test
	public void deleteCustomer_success() {
		String documentNumber = "987654321";
		getCustomer(Mono.just(getCustomerEntity(documentNumber)));
		saveCustomer();
		customerService.deleteCustomer(documentNumber);
	}

	@Test
	public void updateCustomer_phoneExists() {
		String documentNumber = "123456789";
		existsPhone(Mono.just(true));
		getCustomer(Mono.just(getCustomerEntity(documentNumber)));

		assertThrows(Exception.class, () -> {
			customerService.updateCustomer(documentNumber, setContactData());
		});
	}

	@Test
	public void updateCustomer_notFound() {
		String documentNumber = "123456789";
		existsPhone(Mono.just(false));
		getCustomer(Mono.empty());

		assertThrows(Exception.class, () -> {
			customerService.updateCustomer(documentNumber, setContactData());
		});
	}

	@Test
	public void updateCustomer_success() {
		String documentNumber = "987654321";
		existsPhone(Mono.just(false));
		getCustomer(Mono.just(getCustomerEntity(documentNumber)));
		saveCustomer();
		customerService.updateCustomer(documentNumber, setContactData());
	}

	@Test
	public void listCustomer_success() {
		when(customerRepository.findByIsActiveTrue())
				.thenReturn(Flux.fromIterable(
						Arrays.asList(getCustomerEntity("123"), getCustomerEntity("456"))));

		assertNotNull(customerService.findAllCustomers());
	}

	private ContactDataRequest setContactData() {
		ContactDataRequest contactDataRequest = new ContactDataRequest();
		contactDataRequest.setPhoneNumber("123456789");
		contactDataRequest.setEmail("juan.perez@example.com");
		contactDataRequest.setAddress("Calle Falsa 123");
		return contactDataRequest;
	}

	private void getCustomer(Mono<CustomerEntity> customerEntity) {
		when(customerRepository.findByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(customerEntity);
	}

	private CustomerEntity getCustomerEntity(String value) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setId(value);
		customerEntity.setFullName("maria vasquez");
		customerEntity.setEmail(value + "@gmail.com");
		customerEntity.setAddress("calle falsa" + value);
		customerEntity.setPhoneNumber(value);
		customerEntity.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		customerEntity.setDocumentNumber(value);
		customerEntity.setIsActive(true);
		return customerEntity;
	}

	private void existsPhone(Mono<Boolean> exists) {
		when(customerRepository.existsByPhoneNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void existsDocument(Mono<Boolean> exists) {
		when(customerRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private CustomerRequest getCustomerRequest() {
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setFullName("Juan Pérez");
		customerRequest.setPhoneNumber("123456789");
		customerRequest.setDocumentNumber("12345678");
		customerRequest.setEmail("juan.perez@example.com");
		customerRequest.setAddress("Calle Falsa 123");
		customerRequest.setDocumentType(Constants.DOCUMENT_TYPE_DNI);
		customerRequest.setPersonType(Constants.PERSON_TYPE_PERSONAL);
		return customerRequest;
	}

	private void saveCustomer() {
		when(customerRepository.save(any(CustomerEntity.class))).thenAnswer(invocation -> {
			CustomerEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

}
