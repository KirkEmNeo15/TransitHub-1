package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** A request breaks a business rule that is not specific to routes (HTTP 400). */
public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
