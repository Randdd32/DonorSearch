package com.github.randdd32.donor_search_backend.core.util;

import java.util.Locale;

public final class StringUtils {
    /**
     * Преобразует первый символ строки в нижний регистр.
     * Возвращает исходную строку, если она равна null или пуста.
     */
    public static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase(Locale.ROOT) + str.substring(1);
    }

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
