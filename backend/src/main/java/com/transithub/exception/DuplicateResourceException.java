package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** A value that must be unique already exists (HTTP 409). */
public class DuplicateResourceException extends ApiException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
