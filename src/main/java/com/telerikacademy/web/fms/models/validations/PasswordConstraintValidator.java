package com.telerikacademy.web.fms.models.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String>  {
    private int min;
    private int max;

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password == null || password.isBlank() || password.length() >= min && password.length() <= max;
    }
}
