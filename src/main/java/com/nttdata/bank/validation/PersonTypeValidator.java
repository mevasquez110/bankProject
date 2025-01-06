package com.nttdata.bank.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import com.nttdata.bank.validation.impl.PersonTypeValidatorImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PersonTypeValidatorImpl.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonTypeValidator {
	
	String message() default "Invalid customer data";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
