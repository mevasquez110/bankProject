package com.nttdata.bank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import com.nttdata.bank.validation.impl.DepositRequestValidatorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DepositRequestValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DepositRequestValidator {

	/**
	 * The validation message that will be returned if the deposit data is invalid.
	 *
	 * @return The validation message
	 */
	String message() default "Invalid deposit data";

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
