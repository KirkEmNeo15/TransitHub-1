package com.transithub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;

/**
 * Writes our standard error JSON for errors that happen INSIDE the security filter,
 * before a controller is reached (so GlobalExceptionHandler cannot catch them).
 */
final class JsonErrorWriter {

    private JsonErrorWriter() {
    }

    static void write(HttpServletRequest request, HttpServletResponse response,
                      HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status.value()
                + ",\"message\":\"" + escape(message) + "\""
                + ",\"timestamp\":\"" + Instant.now() + "\""
                + ",\"path\":\"" + escape(request.getRequestURI()) + "\""
                + ",\"errors\":[]}");
    }

    private static String escape(String text) {
        StringBuilder escaped = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(c < 0x20 ? ' ' : c);
            }
        }
        return escaped.toString();
    }
}
