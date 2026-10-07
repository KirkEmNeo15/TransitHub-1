package com.transithub.dto;

import java.util.List;

/** One page of results for the admin tables (search + pagination). */
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages) {
}
