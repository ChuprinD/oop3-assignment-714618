package com.moviewatchlist.moviewatchlist;

import java.lang.reflect.Field;

/**
 * Utility class for injecting private fields in test scenarios via reflection.
 */
public class TestUtils {

    /**
     * Set a private field on a target object using reflection.
     *
     * @param target    The object whose field should be set
     * @param fieldName The name of the private field
     * @param value     The value to assign
     */
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject test field", e);
        }
    }
}
