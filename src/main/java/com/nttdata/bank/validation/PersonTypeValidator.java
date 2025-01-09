package com.nttdata.bank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import com.nttdata.bank.validation.impl.PersonTypeValidatorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PersonTypeValidator is a custom validation annotation used to validate
 * customer data based on person type. This annotation is validated by the
 * PersonTypeValidatorImpl class. It includes the validation message, groups,
 * and payload.
 */

@Constraint(validatedBy = PersonTypeValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonTypeValidator {

	/**
	 * The validation message that will be returned if the customer data is invalid.
	 *
	 * @return The validation message
	 */
	String message() default "Invalid customer data";

	/**
	 * Groups for validation (default is empty).
	 *
	 * @return The validation groups
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload for validation (default is empty).
	 *
	 * @return The validation payload
	 */
	Class<? extends Payload>[] payload() default {};
}
