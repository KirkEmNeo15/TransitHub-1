package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** The caller is not logged in, or the login details are wrong (HTTP 401). */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
