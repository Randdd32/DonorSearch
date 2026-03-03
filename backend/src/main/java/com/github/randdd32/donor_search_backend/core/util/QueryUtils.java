package com.github.randdd32.donor_search_backend.core.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
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

    /**
     * Преобразует список строк формата ["field,asc", "field2,desc"] в объект Sort.
     */
    public static Sort createSort(List<String> sortList, Sort defaultSort) {
        if (sortList == null || sortList.isEmpty()) {
            return defaultSort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortInfo : sortList) {
            if (sortInfo == null || sortInfo.isBlank()) continue;

            String[] parts = sortInfo.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            orders.add(new Sort.Order(direction, property));
        }

        return orders.isEmpty() ? defaultSort : Sort.by(orders);
    }

    private QueryUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
