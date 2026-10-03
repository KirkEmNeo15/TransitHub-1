package com.transithub.dto.response;

/** One invalid field in a request, for example field "latitude" with message "Latitude must be between -90 and 90". */
public record FieldErrorDetail(String field, String message) {
}
