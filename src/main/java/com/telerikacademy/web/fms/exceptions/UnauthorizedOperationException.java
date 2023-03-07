package com.company.web.forummanagementsystem.exceptions;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException() {
    }

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
