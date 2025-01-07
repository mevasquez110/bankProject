package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.validation.AccountRequestValidator;
import java.util.Arrays;
import java.util.List;

public class AccountRequestValidatorImpl implements ConstraintValidator<AccountRequestValidator, AccountRequest> {

	private static final List<String> VALID_ACCOUNT_TYPES = Arrays.asList(Constants.ACCOUNT_TYPE_SAVINGS,
			Constants.ACCOUNT_TYPE_CHECKING, Constants.ACCOUNT_TYPE_FIXED_TERM);

	@Override
	public void initialize(AccountRequestValidator constraintAnnotation) {
	}

	@Override
	public boolean isValid(AccountRequest accountRequest, ConstraintValidatorContext context) {
		if (accountRequest == null) {
			return true;
		}

		boolean isValid = true;

		if (accountRequest.getCustomerId() == null || accountRequest.getCustomerId().isEmpty()) {
			addViolation(context, "customerId is mandatory", "customerId");
			isValid = false;
		}

		if (accountRequest.getAccountType() == null || accountRequest.getAccountType().isEmpty()) {
			addViolation(context, "accountType is mandatory", "accountType");
			isValid = false;
		} else if (!VALID_ACCOUNT_TYPES.contains(accountRequest.getAccountType())) {
			addViolation(context, "accountType must be one of 'Ahorro', 'Cuenta corriente', 'Plazo fijo'",
					"accountType");
			isValid = false;
		}

		return isValid;
	}

	private void addViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode).addConstraintViolation();
	}
}
