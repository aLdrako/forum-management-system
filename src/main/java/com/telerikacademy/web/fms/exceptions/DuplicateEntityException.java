package com.telerikacademy.web.fms.exceptions;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException() {
    }

    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists!", type, attribute, value));
    }
}
