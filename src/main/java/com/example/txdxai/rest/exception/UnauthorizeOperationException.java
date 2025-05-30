package com.example.txdxai.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizeOperationException extends RuntimeException {
    public UnauthorizeOperationException(String message) {
        super(message);
    }
}