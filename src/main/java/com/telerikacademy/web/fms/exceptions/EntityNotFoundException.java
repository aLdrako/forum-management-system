package com.company.web.forummanagementsystem.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, long id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found!", type, attribute, value));
    }


    public EntityNotFoundException(String type, Long typeId, String attribute, Long attributeId) {
        super(String.format("%s with id %d and %s %d not found!", type, typeId, attribute, attributeId));
    }
}
