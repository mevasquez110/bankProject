package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.validation.YankiValidator;

public class YankiValidatorImpl implements ConstraintValidator<YankiValidator, YankiRequest> {

	@Override
	public boolean isValid(YankiRequest yankiRequest, ConstraintValidatorContext context) {
		if (yankiRequest == null) {
			return true;
		}

		boolean isValid = true;

		String documentType = yankiRequest.getDocumentType();
		if (!Constants.DOCUMENT_TYPE_CE.equals(documentType) && !Constants.DOCUMENT_TYPE_DNI.equals(documentType)
				&& !Constants.DOCUMENT_TYPE_RUC.equals(documentType)) {
			addViolation(context, "Document type must be 'ce', 'dni' or 'ruc'", "documentType");
			isValid = false;
		}

		return isValid;
	}

	private void addViolation(ConstraintValidatorContext context, String message, String property) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addPropertyNode(property).addConstraintViolation();
	}

}
