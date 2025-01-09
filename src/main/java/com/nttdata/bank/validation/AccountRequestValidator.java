package com.nttdata.bank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import com.nttdata.bank.validation.impl.AccountRequestValidatorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AccountRequestValidator is a custom validation annotation used to validate
 * account request objects. This annotation is validated by the
 * AccountRequestValidatorImpl class. It includes the validation message,
 * groups, and payload.
 */

@Constraint(validatedBy = AccountRequestValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountRequestValidator {

	/**
	 * The validation message that will be returned if the account request is
	 * invalid.
	 *
	 * @return The validation message
	 */
	String message() default "Invalid account request";

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
