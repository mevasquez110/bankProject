package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.AccountRequest;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.validation.AccountRequestValidator;
import java.util.Arrays;
import java.util.List;

/**
 * AccountRequestValidatorImpl is the implementation class for the
 * AccountRequestValidator interface. This class provides the actual validation
 * logic for account request objects. It checks for valid account types and
 * mandatory fields such as customerId and accountType.
 */

public class AccountRequestValidatorImpl implements ConstraintValidator<AccountRequestValidator, AccountRequest> {

	// List of valid account types
	private static final List<String> VALID_ACCOUNT_TYPES = Arrays.asList(Constants.ACCOUNT_TYPE_SAVINGS,
			Constants.ACCOUNT_TYPE_CHECKING, Constants.ACCOUNT_TYPE_FIXED_TERM);

	@Override
	public void initialize(AccountRequestValidator constraintAnnotation) {
		// No initialization required
	}

	/**
	 * Validates an AccountRequest object.
	 *
	 * @param accountRequest The account request to validate
	 * @param context        The context in which the constraint is evaluated
	 * @return true if the account request is valid, false otherwise
	 */
	@Override
	public boolean isValid(AccountRequest accountRequest, ConstraintValidatorContext context) {
		if (accountRequest == null) {
			return true;
		}

		boolean isValid = true;

		// Validate customerId
		if (accountRequest.getCustomerId() == null || accountRequest.getCustomerId().isEmpty()) {
			addViolation(context, "customerId is mandatory", "customerId");
			isValid = false;
		}

		// Validate accountType
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

	/**
	 * Adds a violation to the validation context.
	 *
	 * @param context      The validation context
	 * @param message      The violation message
	 * @param propertyNode The property node associated with the violation
	 */
	private void addViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode).addConstraintViolation();
	}
}
