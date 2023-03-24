package com.telerikacademy.web.fms.models.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhotoConstraintValidator implements ConstraintValidator<PhotoConstraint, String> {
    @Override
    public boolean isValid(String photo, ConstraintValidatorContext context) {
        // TODO to check
//        return photo == null || photo.isEmpty();
        return true;
    }
}
