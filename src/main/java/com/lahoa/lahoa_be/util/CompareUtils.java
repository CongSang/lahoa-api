package com.lahoa.lahoa_be.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.*;

@UtilityClass
public class CompareUtils {

    private static final Set<String> IGNORE = Set.of(
            "createdAt",
            "updatedAt",
            "version"
    );

    public Map<String, Object> diff(Object oldObj, Object newObj) {
        if (oldObj == null || newObj == null) {
            throw new IllegalArgumentException("Objects cannot be null");
        }

        if (!oldObj.getClass().equals(newObj.getClass())) {
            throw new IllegalArgumentException("Objects must be same type");
        }

        Map<String, Object> changes = new LinkedHashMap<>();

        for (Field field : oldObj.getClass().getDeclaredFields()) {
            if (IGNORE.contains(field.getName())) continue;

            field.setAccessible(true);

            try {
                Object oldVal = field.get(oldObj);
                Object newVal = field.get(newObj);

                if (!Objects.deepEquals(oldVal, newVal)) {
                    changes.put(
                            field.getName(),
                            Map.of(
                                    "from", oldVal,
                                    "to", newVal
                            )
                    );
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return changes;
    }
}