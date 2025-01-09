package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.CustomerRequest;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.validation.PersonTypeValidator;

/**
 * PersonTypeValidatorImpl is the implementation class for the
 * PersonTypeValidator interface. This class provides the actual validation
 * logic for customer request objects based on person type. It validates both
 * personal and business customers by checking document types, document numbers,
 * and other relevant fields.
 */

public class PersonTypeValidatorImpl implements ConstraintValidator<PersonTypeValidator, CustomerRequest> {

	@Override
	public boolean isValid(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		if (customerRequest == null) {
			return true;
		}

		boolean isValid = true;

		if (Constants.PERSON_TYPE_PERSONAL.equals(customerRequest.getPersonType())) {
			isValid &= validatePersonalCustomer(customerRequest, context);
		} else if (Constants.PERSON_TYPE_BUSINESS.equals(customerRequest.getPersonType())) {
			isValid &= validateBusinessCustomer(customerRequest, context);
		} else {
			addViolation(context, "Person type must be 'personal' or 'empresarial'", "personType");
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Validates a personal customer request.
	 *
	 * @param customerRequest The customer request to validate
	 * @param context         The context in which the constraint is evaluated
	 * @return true if the personal customer request is valid, false otherwise
	 */
	private boolean validatePersonalCustomer(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (!Constants.DOCUMENT_TYPE_CE.equals(customerRequest.getDocumentType())
				&& !Constants.DOCUMENT_TYPE_DNI.equals(customerRequest.getDocumentType())) {
			addViolation(context, "Document type must be 'CE' or 'DNI' for personal person type", "documentType");
			isValid = false;
		}
		if (Constants.DOCUMENT_TYPE_DNI.equals(customerRequest.getDocumentType())
				&& customerRequest.getDocumentNumber().length() != 8) {
			addViolation(context, "Document number must have 8 digits for DNI", "documentNumber");
			isValid = false;
		}
		if (Constants.DOCUMENT_TYPE_CE.equals(customerRequest.getDocumentType())
				&& customerRequest.getDocumentNumber().length() > 20) {
			addViolation(context, "Document number must have up to 20 digits for CE", "documentNumber");
			isValid = false;
		}
		if (customerRequest.getFullName() == null || customerRequest.getFullName().isEmpty()) {
			addViolation(context, "Full name is mandatory for personal person type", "fullName");
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Validates a business customer request.
	 *
	 * @param customerRequest The customer request to validate
	 * @param context         The context in which the constraint is evaluated
	 * @return true if the business customer request is valid, false otherwise
	 */
	private boolean validateBusinessCustomer(CustomerRequest customerRequest, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (!Constants.DOCUMENT_TYPE_RUC.equals(customerRequest.getDocumentType())) {
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
