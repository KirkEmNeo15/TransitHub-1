package com.transithub.exception;

import org.springframework.http.HttpStatus;

/** Something that was asked for does not exist (HTTP 404). */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id " + id);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
