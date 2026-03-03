package com.github.randdd32.donor_search_backend.core.util;

import java.util.List;

public final class QueryUtils {
    /**
     * Очищает строку поиска от лишних пробелов и приводит к нижнему регистру.
     * Возвращает null, если строка пустая.
     */
    public static String cleanSearchToken(String search) {
        return (search != null && !search.isBlank()) ? search.toLowerCase().trim() : null;
    }

    /**
     * Возвращает null, если список пуст.
     */
    public static <T> List<T> nullIfEmpty(List<T> list) {
        return (list != null && !list.isEmpty()) ? list : null;
    }

    private QueryUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
