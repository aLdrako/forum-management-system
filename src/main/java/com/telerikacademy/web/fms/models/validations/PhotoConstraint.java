package com.telerikacademy.web.fms.models.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhotoConstraintValidator.class)
public @interface PhotoConstraint {
    String message() default "Password should be between {min} and {max} symbols";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
