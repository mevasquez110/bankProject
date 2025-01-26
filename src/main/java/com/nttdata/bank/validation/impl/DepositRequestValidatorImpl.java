package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.validation.DepositRequestValidator;

public class DepositRequestValidatorImpl implements ConstraintValidator<DepositRequestValidator, DepositRequest> {

	@Override
	public void initialize(DepositRequestValidator constraintAnnotation) {
	}

	@Override
	public boolean isValid(DepositRequest depositRequest, ConstraintValidatorContext context) {
		boolean hasDebitCard = isNotEmpty(depositRequest.getDebitCardNumber());
		boolean hasAccountNumber = isNotEmpty(depositRequest.getAccountNumber());

		if (hasDebitCard && hasAccountNumber) {
			addViolation(context, "If either debit card number or account number is provided, the other must be null",
					"accountNumber");
			return false;
		}

		if (!hasDebitCard && !hasAccountNumber) {
			addViolation(context, "Either debit card number or account number must be provided", "accountNumber");
			return false;
		}

		return true;
	}

	private boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private void addViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode).addConstraintViolation();
	}
}
