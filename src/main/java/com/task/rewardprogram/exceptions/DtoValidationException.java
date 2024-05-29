package com.task.rewardprogram.exceptions;

import org.springframework.http.HttpStatus;

public class DtoValidationException extends RuntimeException {

    private final HttpStatus status;

    public DtoValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
