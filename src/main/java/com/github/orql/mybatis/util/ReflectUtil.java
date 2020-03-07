package com.github.orql.mybatis.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ReflectUtil {

    /**
     * 获取List<T> T的类型
     * @param field
     * @return
     */
    public static Class<?> getGenericClazz(Field field) {
        Type genericType = field.getGenericType();
        ParameterizedType pt = (ParameterizedType) genericType;
        // T class type
        return (Class<?>)pt.getActualTypeArguments()[0];
    }

    /**
     * 获取返回值类型
     * 如果List<T>返回T
     * @param method
     * @return
     */
    public static Class<?> getReturnClazz(Method method) {
        if (method.getReturnType() == List.class) {
            ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
            // T class type
            return (Class<?>)pt.getActualTypeArguments()[0];
        }
        return method.getReturnType();
    }

    public static void setValue(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasField(Object instance, String fieldName) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.getName().equals(fieldName)) return true;
        }
        return false;
    }

}
