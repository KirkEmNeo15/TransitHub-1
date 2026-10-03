package com.transithub.exception;

import org.springframework.http.HttpStatus;

/**
 * Parent of all our own exceptions. Each child says which HTTP status it means
 * by overriding getStatus() (polymorphism). In Phase 9 ONE handler uses it to build
 * the error response for every child.
 */
public abstract class ApiException extends RuntimeException {

    protected ApiException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();
}
