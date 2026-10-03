package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** A route request breaks a route rule, for example fewer than 2 stops (HTTP 400). */
public class InvalidRouteException extends ApiException {

    public InvalidRouteException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
