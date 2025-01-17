package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.DepositRequest;
import com.nttdata.bank.validation.DepositRequestValidator;

public class DepositRequestValidatorImpl implements ConstraintValidator<DepositRequestValidator, DepositRequest> {

	@Override
	public void initialize(DepositRequestValidator constraintAnnotation) {
		// No initialization required
	}

	@Override
	public boolean isValid(DepositRequest depositRequest, ConstraintValidatorContext context) {
		if (depositRequest == null) {
			return true;
		}

		boolean isValid = true;

		if (depositRequest.getDebitCardNumber() != null && depositRequest.getAccountNumber() != null) {
			isValid = false;
			addViolation(context, "Account number must be null", "accountNumber");
		} else if (depositRequest.getDebitCardNumber() == null && depositRequest.getAccountNumber() == null) {
			isValid = false;
			addViolation(context, "Debit card number must be null", "debitCardNumber");
		}

		if (!isValid) {
			context.disableDefaultConstraintViolation();
		}

		return isValid;
	}

	private void addViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(propertyNode).addConstraintViolation();
	}

}
