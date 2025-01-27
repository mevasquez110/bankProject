package com.nttdata.bank;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
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
import com.nttdata.bank.service.impl.DebitCardServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.nttdata.bank.entity.DebitCardEntity;
import com.nttdata.bank.repository.AccountRepository;
import com.nttdata.bank.repository.DebitCardRepository;
import com.nttdata.bank.request.AssociateAccountRequest;
import com.nttdata.bank.request.DebitCardRequest;

@SpringBootTest
public class DebitCardServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private DebitCardRepository debitCardRepository;

	@InjectMocks
	private DebitCardServiceImpl debitCardService;

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

	@Test
	public void create_hasDebitCard() {
		DebitCardRequest debitCardRequest = getDebitCardRequest();
		assertTrue(validator.validate(debitCardRequest).isEmpty());
		existsDebitCard(Mono.just(true));

		assertThrows(Exception.class, () -> {
			debitCardService.createDebitCard(debitCardRequest);
		});
	}

	@Test
	public void create_Holder() {
		DebitCardRequest debitCardRequest = getDebitCardRequest();
		assertTrue(validator.validate(debitCardRequest).isEmpty());
		existsDebitCard(Mono.just(false));
		existsHolder(Mono.just(true));

		assertThrows(Exception.class, () -> {
			debitCardService.createDebitCard(debitCardRequest);
		});
	}

	@Test
	public void create_Success() {
		DebitCardRequest debitCardRequest = getDebitCardRequest();
		assertTrue(validator.validate(debitCardRequest).isEmpty());
		existsDebitCard(Mono.just(false));
		existsHolder(Mono.just(false));
		existsDebitCardNumber();
		saveDebitCard();
		debitCardService.createDebitCard(debitCardRequest);
	}

	@Test
	public void associate_NotDebitCard() {
		AssociateAccountRequest associateAccountRequest = new AssociateAccountRequest();
		associateAccountRequest.setPrimaryAccount("12345678996325");

		associateAccountRequest
				.setAssociatedAccounts(Arrays.asList("12345678996963", "12345678996741"));

		assertTrue(validator.validate(associateAccountRequest).isEmpty());
		getDebitCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			debitCardService.associateAccount("12345678", associateAccountRequest);
		});
	}

	@Test
	public void associate_Success() {
		AssociateAccountRequest associateAccountRequest = new AssociateAccountRequest();
		associateAccountRequest.setPrimaryAccount("12345678996325");

		associateAccountRequest
				.setAssociatedAccounts(Arrays.asList("12345678996963", "12345678996741"));

		assertTrue(validator.validate(associateAccountRequest).isEmpty());
		getDebitCard(Mono.just(getDebitCardEntity()));
		existsHolder(Mono.just(false));
		existsDebitCardNumber();
		saveDebitCard();
		debitCardService.associateAccount("12345678", associateAccountRequest);
	}

	@Test
	public void listDebitCards_success() {
		when(debitCardRepository.findByIsActiveTrue()).thenReturn(Flux.empty());
		debitCardService.findAllDebitCard();
	}

	@Test
	public void delete_NotDebitCard() {
		getDebitCard(Mono.empty());

		assertThrows(Exception.class, () -> {
			debitCardService.deleteDebitCard("12345678");
		});
	}

	@Test
	public void delete_success() {
		getDebitCard(Mono.just(getDebitCardEntity()));
		saveDebitCard();
		debitCardService.deleteDebitCard("12345678");
	}

	private void existsDebitCard(Mono<Boolean> exists) {
		when(debitCardRepository.existsByDocumentNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(exists);
	}

	private void existsHolder(Mono<Boolean> exists) {
		when(accountRepository.existsByAccountNumberAndHolderDocAndIsActiveTrue(any(String.class),
				any(String.class)))
				.thenReturn(exists);
	}

	private void existsDebitCardNumber() {
		when(debitCardRepository.existsByDebitCardNumber(any(String.class)))
				.thenReturn(Mono.just(false));
	}

	private void saveDebitCard() {
		when(debitCardRepository.save(any(DebitCardEntity.class))).thenAnswer(invocation -> {
			DebitCardEntity entity = invocation.getArgument(0);
			return Mono.just(entity);
		});
	}

	private void getDebitCard(Mono<DebitCardEntity> debitCardEntity) {
		when(debitCardRepository.findByDebitCardNumberAndIsActiveTrue(any(String.class)))
				.thenReturn(debitCardEntity);
	}

	private DebitCardRequest getDebitCardRequest() {
		DebitCardRequest debitCardRequest = new DebitCardRequest();
		debitCardRequest.setPrimaryAccount("12345678996325");
		debitCardRequest.setDocumentNumber("12345678");
		return debitCardRequest;
	}

	private DebitCardEntity getDebitCardEntity() {
		DebitCardEntity debitCardEntity = new DebitCardEntity();
		debitCardEntity.setPrimaryAccount("12345678996325");
		debitCardEntity.setDocumentNumber("12345678");
		return debitCardEntity;
	}
}
