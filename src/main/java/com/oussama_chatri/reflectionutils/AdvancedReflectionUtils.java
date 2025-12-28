package com.oussama_chatri.reflectionutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Advanced Reflection Utilities with caching, generics handling, proxy creation,
 * deep introspection, and performance optimizations.
 */
public class AdvancedReflectionUtils {

    // Cache for reflection operations to improve performance
    private static final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
    private static final Map<String, Method> methodCache = new ConcurrentHashMap<>();
    private static final Map<String, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

    // ==================== ADVANCED FIELD OPERATIONS ====================

    /**
     * Get field value with caching
     */
    public static Object getFieldValueCached(Object obj, String fieldName) throws Exception {
        String key = obj.getClass().getName() + "." + fieldName;
        Field field = fieldCache.computeIfAbsent(key, k -> {
            try {
                return findFieldInHierarchy(obj.getClass(), fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * Set field value with type conversion
     */
    public static void setFieldValueWithConversion(Object obj, String fieldName, Object value) throws Exception {
        Field field = findFieldInHierarchy(obj.getClass(), fieldName);
        field.setAccessible(true);

        Object convertedValue = convertValue(value, field.getType());
        field.set(obj, convertedValue);
    }

    /**
     * Get all fields matching a predicate
     */
    public static List<Field> getFieldsByPredicate(Class<?> clazz, Predicate<Field> predicate) {
        return getAllFieldsIncludingInherited(clazz).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Get fields with specific annotation
     */
    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getFieldsByPredicate(clazz, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * Get fields by type
     */
    public static List<Field> getFieldsByType(Class<?> clazz, Class<?> fieldType) {
        return getFieldsByPredicate(clazz, field -> field.getType().equals(fieldType));
    }

    /**
     * Deep copy object using reflection
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T source) throws Exception {
        if (source == null) return null;

        Class<?> clazz = source.getClass();
        T copy = (T) createInstanceWithBestConstructor(clazz);

        for (Field field : getAllFieldsIncludingInherited(clazz)) {
            field.setAccessible(true);
            Object value = field.get(source);

            if (value != null && !field.getType().isPrimitive() && !isImmutable(field.getType())) {
                value = deepCopy(value);
            }

            field.set(copy, value);
        }

        return copy;
    }

    /**
     * Map object fields to Map
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        for (Field field : getAllFieldsIncludingInherited(obj.getClass())) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

    /**
     * Create object from Map
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) throws Exception {
        T instance = createInstanceWithBestConstructor(clazz);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Field field = findFieldInHierarchy(clazz, entry.getKey());
                field.setAccessible(true);
                field.set(instance, entry.getValue());
            } catch (NoSuchFieldException e) {
                // Field doesn't exist, skip it
            }
        }

        return instance;
    }

    // ==================== ADVANCED METHOD OPERATIONS ====================

    /**
     * Invoke method with caching
     */
    public static Object invokeMethodCached(Object obj, String methodName, Object... args) throws Exception {
        Class<?>[] paramTypes = getParameterTypes(args);
        String key = obj.getClass().getName() + "." + methodName + ":" + Arrays.toString(paramTypes);

        Method method = methodCache.computeIfAbsent(key, k -> {
            try {
                return findMethodInHierarchy(obj.getClass(), methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
     * Get methods with specific annotation
     */
    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getAllMethodsIncludingInherited(clazz).stream()
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .collect(Collectors.toList());
    }

    /**
     * Get methods by return type
     */
    public static List<Method> getMethodsByReturnType(Class<?> clazz, Class<?> returnType) {
        return getAllMethodsIncludingInherited(clazz).stream()
                .filter(method -> method.getReturnType().equals(returnType))
                .collect(Collectors.toList());
    }

    /**
     * Invoke all methods with specific annotation
     */
    public static List<Object> invokeMethodsWithAnnotation(Object obj, Class<? extends Annotation> annotationClass) throws Exception {
        List<Object> results = new ArrayList<>();

        for (Method method : getMethodsWithAnnotation(obj.getClass(), annotationClass)) {
            if (method.getParameterCount() == 0) {
                method.setAccessible(true);
                results.add(method.invoke(obj));
            }
        }

        return results;
    }

    /**
     * Get method signature as string
     */
    public static String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(Modifier.toString(method.getModifiers())).append(" ");
        sb.append(method.getReturnType().getSimpleName()).append(" ");
        sb.append(method.getName()).append("(");

        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(params[i].getSimpleName());
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Find method by signature pattern
     */
    public static Method findMethodByPattern(Class<?> clazz, String pattern) {
        for (Method method : getAllMethodsIncludingInherited(clazz)) {
            if (getMethodSignature(method).contains(pattern)) {
                return method;
            }
        }
        return null;
    }

    // ==================== GENERICS HANDLING ====================

    /**
     * Get generic type of field
     */
    public static Type getGenericFieldType(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = findFieldInHierarchy(clazz, fieldName);
        return field.getGenericType();
    }

    /**
     * Get generic type arguments
     */
    public static Type[] getGenericTypeArguments(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        return new Type[0];
    }

    /**
     * Get generic superclass type arguments
     */
    public static Type[] getGenericSuperclassTypeArguments(Class<?> clazz) {
        Type superclass = clazz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType) superclass).getActualTypeArguments();
        }
        return new Type[0];
    }

    /**
     * Resolve generic type variable
     */
    public static Class<?> resolveGenericType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = resolveGenericType(componentType);
            return Array.newInstance(componentClass, 0).getClass();
        }
        return Object.class;
    }

    /**
     * Check if type is generic
     */
    public static boolean isGenericType(Type type) {
        return type instanceof ParameterizedType;
    }

    // ==================== PROXY CREATION ====================

    /**
     * Create dynamic proxy with invocation handler
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass },
                handler
        );
    }

    /**
     * Create logging proxy
     */
    public static <T> T createLoggingProxy(T target) {
        return createProxy(target, (proxy, method, args) -> {
            System.out.println("Calling: " + method.getName() + " with args: " + Arrays.toString(args));
            Object result = method.invoke(target, args);
            System.out.println("Result: " + result);
            return result;
        });
    }

    /**
     * Create proxy with method interception
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, InvocationHandler handler) {
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = targetClass.getInterfaces();

        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Target must implement at least one interface");
        }

        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                interfaces,
                handler
        );
    }

    /**
     * Create caching proxy
     */
    public static <T> T createCachingProxy(T target) {
        Map<String, Object> cache = new ConcurrentHashMap<>();

        return createProxy(target, (proxy, method, args) -> {
            String key = method.getName() + Arrays.toString(args);
            return cache.computeIfAbsent(key, k -> {
                try {
                    return method.invoke(target, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    // ==================== ANNOTATION PROCESSING ====================

    /**
     * Get all annotations from class hierarchy
     */
    public static List<Annotation> getAllAnnotations(Class<?> clazz) {
        List<Annotation> annotations = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null) {
            annotations.addAll(Arrays.asList(current.getAnnotations()));
            current = current.getSuperclass();
        }

        return annotations;
    }

    /**
     * Get field annotation with specific type
     */
    public static <T extends Annotation> T getFieldAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) throws NoSuchFieldException {
        Field field = findFieldInHierarchy(clazz, fieldName);
        return field.getAnnotation(annotationClass);
    }

    /**
     * Get method annotation with specific type
     */
    public static <T extends Annotation> T getMethodAnnotation(Class<?> clazz, String methodName, Class<T> annotationClass) throws NoSuchMethodException {
        for (Method method : getAllMethodsIncludingInherited(clazz)) {
            if (method.getName().equals(methodName)) {
                T annotation = method.getAnnotation(annotationClass);
                if (annotation != null) return annotation;
            }
        }
        return null;
    }

    /**
     * Get annotation values as map
     */
    public static Map<String, Object> getAnnotationValues(Annotation annotation) throws Exception {
        Map<String, Object> values = new HashMap<>();

        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            values.put(method.getName(), method.invoke(annotation));
        }

        return values;
    }

    /**
     * Check if class or any superclass has annotation
     */
    public static boolean hasAnnotationInHierarchy(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Class<?> current = clazz;
        while (current != null) {
            if (current.isAnnotationPresent(annotationClass)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    // ==================== CONSTRUCTOR OPERATIONS ====================

    /**
     * Create instance using best matching constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstanceWithBestConstructor(Class<T> clazz, Object... args) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        // Try to find exact match
        Class<?>[] paramTypes = getParameterTypes(args);
        for (Constructor<?> constructor : constructors) {
            if (Arrays.equals(constructor.getParameterTypes(), paramTypes)) {
                constructor.setAccessible(true);
                return (T) constructor.newInstance(args);
            }
        }

        // Try to find compatible match
        for (Constructor<?> constructor : constructors) {
            if (isCompatibleConstructor(constructor, paramTypes)) {
                constructor.setAccessible(true);
                return (T) constructor.newInstance(args);
            }
        }

        // Try default constructor
        try {
            Constructor<T> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new InstantiationException("No suitable constructor found for " + clazz.getName());
        }
    }

    /**
     * Get all constructors from class
     */
    public static List<Constructor<?>> getAllConstructors(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredConstructors());
    }

    /**
     * Get constructor signature
     */
    public static String getConstructorSignature(Constructor<?> constructor) {
        StringBuilder sb = new StringBuilder();
        sb.append(constructor.getDeclaringClass().getSimpleName()).append("(");

        Class<?>[] params = constructor.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(params[i].getSimpleName());
        }

        sb.append(")");
        return sb.toString();
    }

    // ==================== DEEP INTROSPECTION ====================

    /**
     * Get complete class hierarchy
     */
    public static List<Class<?>> getCompleteHierarchy(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null) {
            hierarchy.add(current);
            hierarchy.addAll(Arrays.asList(current.getInterfaces()));
            current = current.getSuperclass();
        }

        return hierarchy;
    }

    /**
     * Compare two objects field by field
     */
    public static Map<String, FieldComparison> compareObjects(Object obj1, Object obj2) throws IllegalAccessException {
        if (obj1.getClass() != obj2.getClass()) {
            throw new IllegalArgumentException("Objects must be of the same type");
        }

        Map<String, FieldComparison> differences = new HashMap<>();

        for (Field field : getAllFieldsIncludingInherited(obj1.getClass())) {
            field.setAccessible(true);
            Object value1 = field.get(obj1);
            Object value2 = field.get(obj2);

            if (!Objects.equals(value1, value2)) {
                differences.put(field.getName(), new FieldComparison(value1, value2));
            }
        }

        return differences;
    }

    /**
     * Get memory footprint of fields (approximate)
     */
    public static long estimateObjectSize(Object obj) {
        long size = 0;

        for (Field field : getAllFieldsIncludingInherited(obj.getClass())) {
            Class<?> type = field.getType();

            if (type.isPrimitive()) {
                if (type == boolean.class || type == byte.class) size += 1;
                else if (type == char.class || type == short.class) size += 2;
                else if (type == int.class || type == float.class) size += 4;
                else if (type == long.class || type == double.class) size += 8;
            } else {
                size += 8; // Reference size
            }
        }

        return size;
    }

    /**
     * Get all declared classes and interfaces in hierarchy
     */
    public static Set<Class<?>> getAllDeclaredTypes(Class<?> clazz) {
        Set<Class<?>> types = new HashSet<>();
        types.add(clazz);

        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            types.addAll(getAllDeclaredTypes(innerClass));
        }

        return types;
    }

    // ==================== TYPE CONVERSION ====================

    /**
     * Convert value to target type
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        // String conversions
        if (targetType == String.class) {
            return value.toString();
        }

        if (value instanceof String) {
            String strValue = (String) value;
            if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(strValue);
            if (targetType == long.class || targetType == Long.class) return Long.parseLong(strValue);
            if (targetType == double.class || targetType == Double.class) return Double.parseDouble(strValue);
            if (targetType == float.class || targetType == Float.class) return Float.parseFloat(strValue);
            if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(strValue);
            if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(strValue);
            if (targetType == short.class || targetType == Short.class) return Short.parseShort(strValue);
        }

        // Number conversions
        if (value instanceof Number) {
            Number numValue = (Number) value;
            if (targetType == int.class || targetType == Integer.class) return numValue.intValue();
            if (targetType == long.class || targetType == Long.class) return numValue.longValue();
            if (targetType == double.class || targetType == Double.class) return numValue.doubleValue();
            if (targetType == float.class || targetType == Float.class) return numValue.floatValue();
            if (targetType == byte.class || targetType == Byte.class) return numValue.byteValue();
            if (targetType == short.class || targetType == Short.class) return numValue.shortValue();
        }

        return value;
    }

    // ==================== HELPER METHODS ====================

    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
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

    private static Method findMethodInHierarchy(Class<?> clazz, String methodName, Class<?>[] paramTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && isCompatibleParameters(method.getParameterTypes(), paramTypes)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz.getName());
    }

    private static List<Field> getAllFieldsIncludingInherited(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }

        return fields;
    }

    private static List<Method> getAllMethodsIncludingInherited(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null) {
            methods.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }

        return methods;
    }

    private static Class<?>[] getParameterTypes(Object... args) {
        if (args == null || args.length == 0) return new Class<?>[0];

        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return types;
    }

    private static boolean isCompatibleParameters(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length != actualTypes.length) return false;

        for (int i = 0; i < declaredTypes.length; i++) {
            if (!isAssignableFrom(declaredTypes[i], actualTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCompatibleConstructor(Constructor<?> constructor, Class<?>[] paramTypes) {
        return isCompatibleParameters(constructor.getParameterTypes(), paramTypes);
    }

    private static boolean isAssignableFrom(Class<?> target, Class<?> source) {
        if (target.isAssignableFrom(source)) return true;

        // Handle primitive wrappers
        Map<Class<?>, Class<?>> primitiveWrappers = new HashMap<>();
        primitiveWrappers.put(int.class, Integer.class);
        primitiveWrappers.put(long.class, Long.class);
        primitiveWrappers.put(double.class, Double.class);
        primitiveWrappers.put(float.class, Float.class);
        primitiveWrappers.put(boolean.class, Boolean.class);
        primitiveWrappers.put(byte.class, Byte.class);
        primitiveWrappers.put(short.class, Short.class);
        primitiveWrappers.put(char.class, Character.class);

        return primitiveWrappers.get(target) == source || primitiveWrappers.get(source) == target;
    }

    private static boolean isImmutable(Class<?> clazz) {
        return clazz == String.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Character.class;
    }

    /**
     * Clear all caches
     */
    public static void clearCache() {
        fieldCache.clear();
        methodCache.clear();
        constructorCache.clear();
    }

    /**
     * Get cache statistics
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("fieldCacheSize", fieldCache.size());
        stats.put("methodCacheSize", methodCache.size());
        stats.put("constructorCacheSize", constructorCache.size());
        return stats;
    }

    // ==================== NESTED CLASSES ====================

    /**
     * Field comparison result
     */
    public static class FieldComparison {
        private final Object value1;
        private final Object value2;

        public FieldComparison(Object value1, Object value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public Object getValue1() { return value1; }
        public Object getValue2() { return value2; }

        @Override
        public String toString() {
            return "FieldComparison{value1=" + value1 + ", value2=" + value2 + "}";
        }
    }

    /**
     * Print detailed class information
     */
    public static void printDetailedClassInfo(Class<?> clazz) {
        System.out.println("=== Detailed Class Information ===");
        System.out.println("Class: " + clazz.getName());
        System.out.println("Package: " + clazz.getPackage().getName());
        System.out.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));

        System.out.println("\n--- Generic Information ---");
        Type[] typeParams = clazz.getTypeParameters();
        if (typeParams.length > 0) {
            System.out.println("Type Parameters: " + Arrays.toString(typeParams));
        }

        System.out.println("\n--- Hierarchy ---");
        for (Class<?> c : getCompleteHierarchy(clazz)) {
            System.out.println("  " + c.getName());
        }

        System.out.println("\n--- Annotations ---");
        for (Annotation annotation : getAllAnnotations(clazz)) {
            System.out.println("  @" + annotation.annotationType().getSimpleName());
        }

        System.out.println("\n--- Fields ---");
        for (Field field : getAllFieldsIncludingInherited(clazz)) {
            System.out.println("  " + Modifier.toString(field.getModifiers()) + " " +
                    field.getType().getSimpleName() + " " + field.getName());
        }

        System.out.println("\n--- Constructors ---");
        for (Constructor<?> constructor : getAllConstructors(clazz)) {
            System.out.println("  " + getConstructorSignature(constructor));
        }

        System.out.println("\n--- Methods ---");
        for (Method method : getAllMethodsIncludingInherited(clazz)) {
            System.out.println("  " + getMethodSignature(method));
        }

        System.out.println("\n--- Size Estimate ---");
        System.out.println("Approximate size: " + estimateObjectSize(clazz) + " bytes");
    }
}
