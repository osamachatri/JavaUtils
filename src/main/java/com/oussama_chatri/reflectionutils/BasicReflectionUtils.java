package com.oussama_chatri.reflectionutils;

import java.lang.reflect.*;
import java.util.*;

/**
 * Basic Reflection Utilities for dynamic field access, method invocation, and class introspection.
 * Provides fundamental operations for runtime type manipulation.
 */
public class BasicReflectionUtils {

    // ==================== FIELD ACCESS ====================

    /**
     * Get field value from an object (handles private fields)
     */
    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (obj == null) throw new IllegalArgumentException("Object cannot be null");

        Field field = findField(obj.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * Set field value on an object (handles private fields)
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        if (obj == null) throw new IllegalArgumentException("Object cannot be null");

        Field field = findField(obj.getClass(), fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * Get static field value from a class
     */
    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(clazz, fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    /**
     * Set static field value on a class
     */
    public static void setStaticFieldValue(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(clazz, fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Find field in class hierarchy
     */
    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in " + clazz.getName());
    }

    /**
     * Get all fields including inherited ones
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * Get all field names from a class
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> names = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            names.add(field.getName());
        }
        return names;
    }

    // ==================== METHOD INVOCATION ====================

    /**
     * Invoke method with arguments (handles private methods)
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) throws Exception {
        if (obj == null) throw new IllegalArgumentException("Object cannot be null");

        Class<?>[] paramTypes = getParameterTypes(args);
        Method method = findMethod(obj.getClass(), methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
     * Invoke static method with arguments
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object... args) throws Exception {
        Class<?>[] paramTypes = getParameterTypes(args);
        Method method = findMethod(clazz, methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    /**
     * Invoke method without arguments
     */
    public static Object invokeMethod(Object obj, String methodName) throws Exception {
        return invokeMethod(obj, methodName, new Object[0]);
    }

    /**
     * Find method in class hierarchy with parameter matching
     */
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // Try to find method with compatible parameters
                for (Method method : current.getDeclaredMethods()) {
                    if (method.getName().equals(methodName) && isCompatible(method.getParameterTypes(), paramTypes)) {
                        return method;
                    }
                }
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz.getName());
    }

    /**
     * Get all methods including inherited ones
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null) {
            methods.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }
        return methods;
    }

    /**
     * Get method names from a class
     */
    public static List<String> getMethodNames(Class<?> clazz) {
        List<String> names = new ArrayList<>();
        for (Method method : getAllMethods(clazz)) {
            names.add(method.getName());
        }
        return names;
    }

    // ==================== CLASS INTROSPECTION ====================

    /**
     * Create instance of class using default constructor
     */
    public static <T> T createInstance(Class<T> clazz) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * Create instance with constructor arguments
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) throws Exception {
        Class<?>[] paramTypes = getParameterTypes(args);
        Constructor<T> constructor = findConstructor(clazz, paramTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    /**
     * Find constructor with matching parameters
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>[] paramTypes) throws NoSuchMethodException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (isCompatible(constructor.getParameterTypes(), paramTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        throw new NoSuchMethodException("Constructor not found for " + clazz.getName());
    }

    /**
     * Get all interfaces implemented by a class
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        List<Class<?>> interfaces = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null) {
            interfaces.addAll(Arrays.asList(current.getInterfaces()));
            current = current.getSuperclass();
        }
        return interfaces;
    }

    /**
     * Get all superclasses
     */
    public static List<Class<?>> getSuperclasses(Class<?> clazz) {
        List<Class<?>> superclasses = new ArrayList<>();
        Class<?> current = clazz.getSuperclass();
        while (current != null) {
            superclasses.add(current);
            current = current.getSuperclass();
        }
        return superclasses;
    }

    /**
     * Check if class has annotation
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }

    /**
     * Get annotation from class
     */
    public static <T extends java.lang.annotation.Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * Check if field has annotation
     */
    public static boolean hasFieldAnnotation(Class<?> clazz, String fieldName, Class<? extends java.lang.annotation.Annotation> annotationClass) throws NoSuchFieldException {
        Field field = findField(clazz, fieldName);
        return field.isAnnotationPresent(annotationClass);
    }

    /**
     * Check if method has annotation
     */
    public static boolean hasMethodAnnotation(Class<?> clazz, String methodName, Class<? extends java.lang.annotation.Annotation> annotationClass) throws NoSuchMethodException {
        for (Method method : getAllMethods(clazz)) {
            if (method.getName().equals(methodName) && method.isAnnotationPresent(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get class by name
     */
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    /**
     * Check if class is abstract
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * Check if class is interface
     */
    public static boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }

    /**
     * Check if class is enum
     */
    public static boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }

    /**
     * Get simple class name without package
     */
    public static String getSimpleName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    /**
     * Get package name
     */
    public static String getPackageName(Class<?> clazz) {
        return clazz.getPackage().getName();
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get parameter types from arguments
     */
    private static Class<?>[] getParameterTypes(Object... args) {
        if (args == null || args.length == 0) return new Class<?>[0];

        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    /**
     * Check if parameter types are compatible
     */
    private static boolean isCompatible(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length != actualTypes.length) return false;

        for (int i = 0; i < declaredTypes.length; i++) {
            if (!isAssignable(declaredTypes[i], actualTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if target type is assignable from source type
     */
    private static boolean isAssignable(Class<?> target, Class<?> source) {
        if (target.isAssignableFrom(source)) return true;

        // Handle primitive types
        if (target.isPrimitive()) {
            if (target == int.class && source == Integer.class) return true;
            if (target == long.class && source == Long.class) return true;
            if (target == double.class && source == Double.class) return true;
            if (target == float.class && source == Float.class) return true;
            if (target == boolean.class && source == Boolean.class) return true;
            if (target == byte.class && source == Byte.class) return true;
            if (target == short.class && source == Short.class) return true;
            if (target == char.class && source == Character.class) return true;
        }

        return false;
    }

    /**
     * Copy all fields from source to target object
     */
    public static void copyFields(Object source, Object target) throws IllegalAccessException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target cannot be null");
        }

        for (Field field : getAllFields(source.getClass())) {
            try {
                Field targetField = findField(target.getClass(), field.getName());
                if (targetField.getType().equals(field.getType())) {
                    field.setAccessible(true);
                    targetField.setAccessible(true);
                    targetField.set(target, field.get(source));
                }
            } catch (NoSuchFieldException e) {
                // Field doesn't exist in target, skip it
            }
        }
    }

    /**
     * Print class information for debugging
     */
    public static void printClassInfo(Class<?> clazz) {
        System.out.println("Class: " + clazz.getName());
        System.out.println("Package: " + getPackageName(clazz));
        System.out.println("Simple Name: " + getSimpleName(clazz));
        System.out.println("Is Interface: " + isInterface(clazz));
        System.out.println("Is Abstract: " + isAbstract(clazz));
        System.out.println("Is Enum: " + isEnum(clazz));

        System.out.println("\nFields:");
        for (Field field : getAllFields(clazz)) {
            System.out.println("  - " + field.getName() + " : " + field.getType().getSimpleName());
        }

        System.out.println("\nMethods:");
        for (Method method : getAllMethods(clazz)) {
            System.out.println("  - " + method.getName() + "(" + getParameterString(method) + ") : " + method.getReturnType().getSimpleName());
        }
    }

    private static String getParameterString(Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (params.length == 0) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(params[i].getSimpleName());
        }
        return sb.toString();
    }
}
