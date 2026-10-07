package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** Cannot delete something that other data still uses (HTTP 409). */
public class ResourceInUseException extends ApiException {

    public ResourceInUseException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
