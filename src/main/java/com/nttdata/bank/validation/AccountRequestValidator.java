package com.nttdata.bank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import com.nttdata.bank.validation.impl.AccountRequestValidatorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountRequestValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountRequestValidator {
	String message() default "Invalid account request";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
