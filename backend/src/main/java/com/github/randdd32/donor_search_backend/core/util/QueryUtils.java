package com.github.randdd32.donor_search_backend.core.util;

public final class QueryUtils {
    /**
     * Очищает строку поиска от лишних пробелов и приводит к нижнему регистру.
     * Возвращает null, если строка пустая.
     */
    public static String cleanSearchToken(String search) {
        return (search != null && !search.isBlank()) ? search.toLowerCase().trim() : null;
    }

    private QueryUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
