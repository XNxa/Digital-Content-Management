package com.dcm.backend.utils;

import com.dcm.backend.annotations.LogIgnore;

import java.lang.reflect.Field;

public class Logger {

    /**
     * Return a representation of the object without the fields annotated with {@code
     *
     * @param object
     * @return
     * @LogIgnore}
     */
    public static String toString(Object object) {
        if (object == null) {
            return "";
        }

        StringBuilder res = new StringBuilder();
        res.append(object.getClass().getSimpleName()).append("(");

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(LogIgnore.class)) {
                field.setAccessible(true);
                try {
                    res.append(field.getName()).append("=").append(field.get(object)).append(", ");
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
        res.delete(res.length() - 2, res.length()).append(")");
        return res.toString();
    }

}
