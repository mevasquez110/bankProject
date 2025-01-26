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
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditRequest;

@SpringBootTest
public class OperationServiceTest {

	@Autowired
	private Validator validator;

	@Mock
	private OperationService operationService;

	@InjectMocks
	private OperationServiceTest operationServiceTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInvalidDepositRequestWithNullAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithBothDebitCardAndAccountNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAccountNumber("987654321");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNeitherDebitCardNorAccountNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNegativeAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(-500.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithZeroAmount() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(0.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testValidDepositRequestWithOnlyDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("");
		depositRequest.setAccountNumber("");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testValidDepositRequestTrim() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("  ");
		depositRequest.setAccountNumber("  ");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithNullDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidNull() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidDepositRequestWithEmptyDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setDocumentNumber("");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	public void testInvalidWithdrawalRequestWithMissingDebitCardNumber() {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setDocumentNumber("12345678");
		withdrawalRequest.setAmount(500.0);
		assertFalse(validator.validate(withdrawalRequest).isEmpty());
	}

	@Test
	public void testInvalidAccountTransferRequestWithMissingAccountNumberWithdraws() {
		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberReceive("987654321");
		accountTransferRequest.setDocumentNumberWithdraws("12345678");
		accountTransferRequest.setDocumentNumberReceive("87654321");
		accountTransferRequest.setAmount(1000.0);
		assertFalse(validator.validate(accountTransferRequest).isEmpty());
	}

	@Test
	public void testInvalidMobileTransferRequestWithMissingMobileNumberWithdraws() {
		MobileTransferRequest mobileTransferRequest = new MobileTransferRequest();
		mobileTransferRequest.setMobileNumberReceive("987654321");
		mobileTransferRequest.setDocumentNumberWithdraws("12345678");
		mobileTransferRequest.setDocumentNumberReceive("87654321");
		mobileTransferRequest.setAmount(200.0);
		assertFalse(validator.validate(mobileTransferRequest).isEmpty());
	}



	@Test
	public void testInvalidPayCreditRequestWithMissingCreditId() {
		PayCreditRequest payCreditRequest = new PayCreditRequest();
		payCreditRequest.setAmount(1000.0);
		payCreditRequest.setDocumentNumber("12345678");
		payCreditRequest.setAccountNumber("987654321");
		assertFalse(validator.validate(payCreditRequest).isEmpty());
	}

}
