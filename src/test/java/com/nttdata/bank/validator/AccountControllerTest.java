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
import com.nttdata.bank.service.AccountsService;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.request.UpdateAccountRequest;
import com.nttdata.bank.response.AccountResponse;
import com.nttdata.bank.response.BalanceResponse;

@SpringBootTest
public class AccountControllerTest {

	@Autowired
	private Validator validator;

	@Mock
	private AccountsService accountsService;

	@InjectMocks
	private AccountControllerTest accountControllerTest;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Order(1)
	public void testInvalidAccountRequestWithMissingAccountType() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setOpeningAmount(100.0);
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	@Order(2)
	public void testInvalidAccountRequestWithNegativeOpeningAmount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_SAVINGS);
		accountRequest.setOpeningAmount(-50.0);
		assertFalse(validator.validate(accountRequest).isEmpty());
	}

	@Test
	@Order(3)
	public void testValidAccountRequest() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_CHECKING);
		accountRequest.setOpeningAmount(100.0);
	    accountRequest.setHolderDoc(Arrays.asList("12547856"));

		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

	@Test
	@Order(4)
	public void testCheckBalance() {
		String accountNumber = "123456789";
		BalanceResponse balanceResponse = new BalanceResponse();
		Mockito.when(accountsService.checkBalance(accountNumber)).thenReturn(balanceResponse);

		BalanceResponse result = accountsService.checkBalance(accountNumber);
		assertNotNull(result);
		Mockito.verify(accountsService).checkBalance(accountNumber);
	}

	@Test
	@Order(5)
	public void testFindAllAccounts() {
		String documentNumber = "12345678";
		List<AccountResponse> accountList = Arrays.asList(new AccountResponse(), new AccountResponse());
		Mockito.when(accountsService.findAllAccounts(documentNumber)).thenReturn(accountList);

		List<AccountResponse> result = accountsService.findAllAccounts(documentNumber);
		assertNotNull(result);
		Mockito.verify(accountsService).findAllAccounts(documentNumber);
	}

	@Test
	@Order(6)
	public void testUpdateAccount() {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber("123456789");
		updateAccountRequest.setAmount(200.0);
		
		assertTrue(validator.validate(updateAccountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.updateAccount(updateAccountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.updateAccount(updateAccountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).updateAccount(updateAccountRequest);
	}

	@Test
	@Order(7)
	public void testUpdateAccountWithInvalidRequest() {
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest();
		updateAccountRequest.setAccountNumber("123456789");
		updateAccountRequest.setAmount(-200.0);
		assertFalse(validator.validate(updateAccountRequest).isEmpty());
	}

	@Test
	@Order(8)
	public void testDeleteAccount() {
		String accountNumber = "123456789";
		accountsService.deleteAccount(accountNumber);
		Mockito.verify(accountsService).deleteAccount(accountNumber);
	}

	@Test
	@Order(9)
	public void testValidSavingsAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_SAVINGS);
		accountRequest.setOpeningAmount(100.0);
	    accountRequest.setHolderDoc(Arrays.asList("12345678"));

		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

	@Test
	@Order(10)
	public void testValidCheckingAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_CHECKING);
		accountRequest.setOpeningAmount(200.0);
	    accountRequest.setHolderDoc(Arrays.asList("78965412"));

		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

	@Test
	@Order(11)
	public void testValidFixedTermAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_FIXED_TERM);
		accountRequest.setOpeningAmount(500.0);
	    accountRequest.setHolderDoc(Arrays.asList("45896325"));
	    
		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

	@Test
	@Order(12)
	public void testValidVIPAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_VIP);
		accountRequest.setOpeningAmount(1000.0);
	    accountRequest.setHolderDoc(Arrays.asList("41557896"));

		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

	@Test
	@Order(13)
	public void testValidPYMEAccount() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setAccountType(Constants.ACCOUNT_TYPE_PYME);
		accountRequest.setOpeningAmount(2000.0);
	    accountRequest.setHolderDoc(Arrays.asList("65896325"));

		assertTrue(validator.validate(accountRequest).isEmpty());

		AccountResponse accountResponse = new AccountResponse();
		Mockito.when(accountsService.registerAccount(accountRequest)).thenReturn(accountResponse);

		AccountResponse result = accountsService.registerAccount(accountRequest);
		assertNotNull(result);
		Mockito.verify(accountsService).registerAccount(accountRequest);
	}

}
