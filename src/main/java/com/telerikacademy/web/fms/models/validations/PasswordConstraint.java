package com.telerikacademy.web.fms.models.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintValidator.class)
public @interface PasswordConstraint {
    String message() default "Password should be between {min} and {max} symbols";
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

