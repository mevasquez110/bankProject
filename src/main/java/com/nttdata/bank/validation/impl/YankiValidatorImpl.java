package com.nttdata.bank.validation.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.util.Constants;
import com.nttdata.bank.validation.YankiValidator;

/**
 * YankiValidatorImpl is the implementation class for the YankiValidator interface.
 * This class provides the actual logic for validating Yanki requests.
 */
public class YankiValidatorImpl implements ConstraintValidator<YankiValidator, YankiRequest> {

    /**
     * Validates the Yanki request. This method checks if the request is null 
     * and verifies the document type.
     *
     * @param yankiRequest the Yanki request containing the details to validate
     * @param context the constraint validator context
     * @return true if the Yanki request is valid, false otherwise
     */
    @Override
    public boolean isValid(YankiRequest yankiRequest, ConstraintValidatorContext context) {
        boolean isValid = true;

        String documentType = yankiRequest.getDocumentType();
        if (!Constants.DOCUMENT_TYPE_CE.equals(documentType) && !Constants.DOCUMENT_TYPE_DNI.equals(documentType)
                && !Constants.DOCUMENT_TYPE_RUC.equals(documentType)) {
            addViolation(context, "Document type must be 'ce', 'dni' or 'ruc'", "documentType");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Adds a violation to the constraint validator context.
     *
     * @param context the constraint validator context
     * @param message the validation message
     * @param property the property node to which the violation applies
     */
    private void addViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(property).addConstraintViolation();
    }

}
