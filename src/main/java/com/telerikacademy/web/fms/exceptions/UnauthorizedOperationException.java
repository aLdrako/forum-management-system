package com.telerikacademy.web.fms.exceptions;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException() {
    }

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
