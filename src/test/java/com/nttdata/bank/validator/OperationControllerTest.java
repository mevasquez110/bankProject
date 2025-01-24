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
import com.nttdata.bank.service.OperationService;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.request.WithdrawalRequest;
import com.nttdata.bank.request.AccountTransferRequest;
import com.nttdata.bank.request.MobileTransferRequest;
import com.nttdata.bank.request.PayCreditCardRequest;
import com.nttdata.bank.request.PayCreditRequest;
import com.nttdata.bank.response.TransactionResponse;
import com.nttdata.bank.response.ProductResponse;

@SpringBootTest
public class OperationControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private OperationService operationService;

	@InjectMocks
	private OperationControllerTest operationControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidDepositRequestWithMissingDocumentNumber() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAccountNumber("987654321");
		depositRequest.setAmount(1000.0);
		assertFalse(validator.validate(depositRequest).isEmpty());
	}

	@Test
	@Order(2)
	public void testValidDepositRequest() {
		DepositRequest depositRequest = new DepositRequest();
		depositRequest.setDebitCardNumber("123456789");
		depositRequest.setAccountNumber("987654321");
		depositRequest.setDocumentNumber("12345678");
		depositRequest.setAmount(1000.0);
		assertTrue(validator.validate(depositRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.makeDeposit(depositRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.makeDeposit(depositRequest);
		assertNotNull(result);
		Mockito.verify(operationService).makeDeposit(depositRequest);
	}

	@Test
	@Order(3)
	public void testInvalidWithdrawalRequestWithMissingDebitCardNumber() {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setDocumentNumber("12345678");
		withdrawalRequest.setAmount(500.0);
		assertFalse(validator.validate(withdrawalRequest).isEmpty());
	}

	@Test
	@Order(4)
	public void testValidWithdrawalRequest() {
		WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
		withdrawalRequest.setDebitCardNumber("123456789");
		withdrawalRequest.setDocumentNumber("12345678");
		withdrawalRequest.setAmount(500.0);
		assertTrue(validator.validate(withdrawalRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.makeWithdrawal(withdrawalRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.makeWithdrawal(withdrawalRequest);
		assertNotNull(result);
		Mockito.verify(operationService).makeWithdrawal(withdrawalRequest);
	}

	@Test
	@Order(5)
	public void testInvalidAccountTransferRequestWithMissingAccountNumberWithdraws() {
		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberReceive("987654321");
		accountTransferRequest.setDocumentNumberWithdraws("12345678");
		accountTransferRequest.setDocumentNumberReceive("87654321");
		accountTransferRequest.setAmount(1000.0);
		assertFalse(validator.validate(accountTransferRequest).isEmpty());
	}

	@Test
	@Order(6)
	public void testValidAccountTransferRequest() {
		AccountTransferRequest accountTransferRequest = new AccountTransferRequest();
		accountTransferRequest.setAccountNumberWithdraws("123456789");
		accountTransferRequest.setAccountNumberReceive("987654321");
		accountTransferRequest.setDocumentNumberWithdraws("12345678");
		accountTransferRequest.setDocumentNumberReceive("87654321");
		accountTransferRequest.setAmount(1000.0);
		assertTrue(validator.validate(accountTransferRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.makeAccountTransfer(accountTransferRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.makeAccountTransfer(accountTransferRequest);
		assertNotNull(result);
		Mockito.verify(operationService).makeAccountTransfer(accountTransferRequest);
	}

	@Test
	@Order(7)
	public void testInvalidMobileTransferRequestWithMissingMobileNumberWithdraws() {
		MobileTransferRequest mobileTransferRequest = new MobileTransferRequest();
		mobileTransferRequest.setMobileNumberReceive("987654321");
		mobileTransferRequest.setDocumentNumberWithdraws("12345678");
		mobileTransferRequest.setDocumentNumberReceive("87654321");
		mobileTransferRequest.setAmount(200.0);
		assertFalse(validator.validate(mobileTransferRequest).isEmpty());
	}

	@Test
	@Order(8)
	public void testValidMobileTransferRequest() {
		MobileTransferRequest mobileTransferRequest = new MobileTransferRequest();
		mobileTransferRequest.setMobileNumberWithdraws("123456789");
		mobileTransferRequest.setMobileNumberReceive("987654321");
		mobileTransferRequest.setDocumentNumberWithdraws("12345678");
		mobileTransferRequest.setDocumentNumberReceive("87654321");
		mobileTransferRequest.setAmount(200.0);
		assertTrue(validator.validate(mobileTransferRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.makeMobileTransfer(mobileTransferRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.makeMobileTransfer(mobileTransferRequest);
		assertNotNull(result);
		Mockito.verify(operationService).makeMobileTransfer(mobileTransferRequest);
	}

	@Test
	@Order(9)
	public void testInvalidPayCreditCardRequestWithMissingCreditCardNumber() {
		PayCreditCardRequest payCreditCardRequest = new PayCreditCardRequest();
		payCreditCardRequest.setAmount(1000.0);
		assertFalse(validator.validate(payCreditCardRequest).isEmpty());
	}

	@Test
	@Order(10)
	public void testValidPayCreditCardRequest() {
		PayCreditCardRequest payCreditCardRequest = new PayCreditCardRequest();
		payCreditCardRequest.setCreditCardNumber("123456789");
		payCreditCardRequest.setAmount(1000.0);
		assertTrue(validator.validate(payCreditCardRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.payCreditCard(payCreditCardRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.payCreditCard(payCreditCardRequest);
		assertNotNull(result);
		Mockito.verify(operationService).payCreditCard(payCreditCardRequest);
	}

	@Test
	@Order(11)
	public void testInvalidPayCreditRequestWithMissingCreditId() {
		PayCreditRequest payCreditRequest = new PayCreditRequest();
		payCreditRequest.setAmount(1000.0);
		payCreditRequest.setDocumentNumber("12345678");
		payCreditRequest.setAccountNumber("987654321");
		assertFalse(validator.validate(payCreditRequest).isEmpty());
	}

	@Test
	@Order(12)
	public void testValidPayCreditRequest() {
		PayCreditRequest payCreditRequest = new PayCreditRequest();
		payCreditRequest.setCreditId("CREDIT123");
		payCreditRequest.setAmount(1000.0);
		payCreditRequest.setDocumentNumber("12345678");
		payCreditRequest.setAccountNumber("987654321");
		assertTrue(validator.validate(payCreditRequest).isEmpty());

		TransactionResponse transactionResponse = new TransactionResponse();
		Mockito.when(operationService.payCredit(payCreditRequest)).thenReturn(transactionResponse);

		TransactionResponse result = operationService.payCredit(payCreditRequest);
		assertNotNull(result);
		Mockito.verify(operationService).payCredit(payCreditRequest);
	}

	@Test
	@Order(13)
	public void testCheckTransactions() {
		String documentNumber = "12345678";
		List<TransactionResponse> transactionList = Arrays.asList(new TransactionResponse(), new TransactionResponse());
		Mockito.when(operationService.checkTransactions(documentNumber)).thenReturn(transactionList);

		List<TransactionResponse> result = operationService.checkTransactions(documentNumber);
		assertNotNull(result);
		Mockito.verify(operationService).checkTransactions(documentNumber);
	}

	@Test
	@Order(14)
	public void testGetProducts() {
		String documentNumber = "12345678";
		List<ProductResponse> productList = Arrays.asList(new ProductResponse(), new ProductResponse());
		Mockito.when(operationService.getProducts(documentNumber)).thenReturn(productList);

		List<ProductResponse> result = operationService.getProducts(documentNumber);
		assertNotNull(result);
		Mockito.verify(operationService).getProducts(documentNumber);
	}
}
