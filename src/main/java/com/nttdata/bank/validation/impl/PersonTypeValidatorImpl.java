package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.validation.PersonTypeValidator;

public class PersonTypeValidatorImpl implements ConstraintValidator<PersonTypeValidator, CustomerRequest> {

	@Override
	public boolean isValid(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		if (customerRequest == null) {
			return true;
		}

		boolean isValid = true;

		if ("personal".equals(customerRequest.getPersonType())) {
			isValid &= validatePersonalCustomer(customerRequest, context);
		} else if ("empresarial".equals(customerRequest.getPersonType())) {
			isValid &= validateBusinessCustomer(customerRequest, context);
		} else {
			addViolation(context, "Person type must be 'personal' or 'empresarial'", "personType");
			isValid = false;
		}

		return isValid;
	}

	private boolean validatePersonalCustomer(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (!"CE".equals(customerRequest.getDocumentType()) && !"DNI".equals(customerRequest.getDocumentType())) {
			addViolation(context, "Document type must be 'CE' or 'DNI' for personal person type", "documentType");
			isValid = false;
		}
		if ("DNI".equals(customerRequest.getDocumentType()) && customerRequest.getDocumentNumber().length() != 8) {
			addViolation(context, "Document number must have 8 digits for DNI", "documentNumber");
			isValid = false;
		}
		if ("CE".equals(customerRequest.getDocumentType()) && customerRequest.getDocumentNumber().length() > 20) {
			addViolation(context, "Document number must have up to 20 digits for CE", "documentNumber");
			isValid = false;
		}
		if (customerRequest.getFullName() == null || customerRequest.getFullName().isEmpty()) {
			addViolation(context, "Full name is mandatory for personal person type", "fullName");
			isValid = false;
		}

		return isValid;
	}

	private boolean validateBusinessCustomer(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (!"RUC".equals(customerRequest.getDocumentType())) {
			addViolation(context, "Document type must be 'RUC' for empresarial person type", "documentType");
			isValid = false;
		}
		if (customerRequest.getCompanyName() == null || customerRequest.getCompanyName().isEmpty()) {
			addViolation(context, "Company name is mandatory for empresarial person type", "companyName");
			isValid = false;
		}
		if (customerRequest.getDocumentNumber().length() != 11) {
			addViolation(context, "Document number must have 11 digits for RUC", "documentNumber");
			isValid = false;
		}

		return isValid;
	}

	private void addViolation(ConstraintValidatorContext context, String message, String propertyNode) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
		.addPropertyNode(propertyNode)
		.addConstraintViolation();
	}
}