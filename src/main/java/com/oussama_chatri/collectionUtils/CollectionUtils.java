package com.oussama_chatri.collectionUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class CollectionUtils {

    public static <T> T safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public static <T> T safeGet(List<T> list, int index, T defaultValue) {
        if (list == null || index < 0 || index >= list.size()) {
            return defaultValue;
        }
        return list.get(index);
    }

    public static <K, V> V safeGet(Map<K, V> map, K key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static <K, V> V safeGet(Map<K, V> map, K key, V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        return map.getOrDefault(key, defaultValue);
    }

    public static <T> T safeFirst(List<T> list) {
        return safeGet(list, 0);
    }

    public static <T> T safeFirst(List<T> list, T defaultValue) {
        return safeGet(list, 0, defaultValue);
    }

    public static <T> T safeLast(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static <T> T safeLast(List<T> list, T defaultValue) {
        if (list == null || list.isEmpty()) {
            return defaultValue;
        }
        return list.get(list.size() - 1);
    }

    public static <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        fromIndex = Math.max(0, fromIndex);
        toIndex = Math.min(list.size(), toIndex);
        if (fromIndex >= toIndex) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> List<T> deepCopy(List<T> list) {
        if (list == null) {
            return null;
        }
        return new ArrayList<>(list);
    }

    public static <T> Set<T> deepCopy(Set<T> set) {
        if (set == null) {
            return null;
        }
        return new HashSet<>(set);
    }

    public static <K, V> Map<K, V> deepCopy(Map<K, V> map) {
        if (map == null) {
            return null;
        }
        return new HashMap<>(map);
    }

    public static <T> List<List<T>> deepCopyNestedList(List<List<T>> nestedList) {
        if (nestedList == null) {
            return null;
        }
        List<List<T>> copy = new ArrayList<>();
        for (List<T> list : nestedList) {
            copy.add(list != null ? new ArrayList<>(list) : null);
        }
        return copy;
    }

    public static <K, V> Map<K, V> deepCopyMap(Map<K, V> map) {
        if (map == null) {
            return null;
        }
        Map<K, V> copy = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map, BiPredicate<K, V> predicate) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getKey(), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> filterByKey(Map<K, V> map, Predicate<K> predicate) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .filter(e -> predicate.test(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <T> List<T> filterNulls(List<T> list) {
        return filter(list, Objects::nonNull);
    }

    public static <K, V> Map<K, V> filterNullValues(Map<K, V> map) {
        return filterByValue(map, Objects::nonNull);
    }

    public static <K, V> Map<K, V> filterNullKeys(Map<K, V> map) {
        return filterByKey(map, Objects::nonNull);
    }

    public static <T> List<T> distinct(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new LinkedHashSet<>();
        if (list1 != null) set.addAll(list1);
        if (list2 != null) set.addAll(list2);
        return new ArrayList<>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(list1);
        result.retainAll(list2);
        return result;
    }

    public static <T> List<T> difference(List<T> list1, List<T> list2) {
        if (list1 == null) {
            return new ArrayList<>();
        }
        if (list2 == null) {
            return new ArrayList<>(list1);
        }
        List<T> result = new ArrayList<>(list1);
        result.removeAll(list2);
        return result;
    }

    public static <T> List<T> concat(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            if (list != null) {
                result.addAll(list);
            }
        }
        return result;
    }

    public static <T> List<T> reverse(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    public static <T> List<T> shuffle(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public static <T> List<T> shuffle(List<T> list, Random random) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, random);
        return shuffled;
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || size <= 0) {
            return new ArrayList<>();
        }
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return partitions;
    }

    public static <T> List<List<T>> chunkByCount(List<T> list, int chunks) {
        if (list == null || chunks <= 0) {
            return new ArrayList<>();
        }
        int chunkSize = (int) Math.ceil((double) list.size() / chunks);
        return partition(list, chunkSize);
    }

    public static <T> Map<Boolean, List<T>> partitionBy(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return Map.of(true, new ArrayList<>(), false, new ArrayList<>());
        }
        return list.stream().collect(Collectors.partitioningBy(predicate));
    }

    public static <T, K> Map<K, List<T>> groupBy(List<T> list, Function<T, K> classifier) {
        if (list == null) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.groupingBy(classifier));
    }

    public static <T> List<T> flatten(List<List<T>> nestedList) {
        if (nestedList == null) {
            return new ArrayList<>();
        }
        return nestedList.stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <K, V, R> Map<K, R> mapValues(Map<K, V> map, Function<V, R> mapper) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> mapper.apply(e.getValue())));
    }

    public static <K, V, R> Map<R, V> mapKeys(Map<K, V> map, Function<K, R> mapper) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(e -> mapper.apply(e.getKey()), Map.Entry::getValue));
    }

    public static <T> T reduce(List<T> list, T identity, BinaryOperator<T> accumulator) {
        if (list == null) {
            return identity;
        }
        return list.stream().reduce(identity, accumulator);
    }

    public static <T> Optional<T> reduce(List<T> list, BinaryOperator<T> accumulator) {
        if (list == null) {
            return Optional.empty();
        }
        return list.stream().reduce(accumulator);
    }

    public static <T> List<T> take(List<T> list, int n) {
        if (list == null || n <= 0) {
            return new ArrayList<>();
        }
        return list.stream().limit(n).collect(Collectors.toList());
    }

    public static <T> List<T> skip(List<T> list, int n) {
        if (list == null || n <= 0) {
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        }
        return list.stream().skip(n).collect(Collectors.toList());
    }

    public static <T> List<T> takeWhile(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().takeWhile(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> dropWhile(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().dropWhile(predicate).collect(Collectors.toList());
    }

    public static <T> boolean contains(Collection<T> collection, T element) {
        return collection != null && collection.contains(element);
    }

    public static <T> boolean containsAny(Collection<T> collection, Collection<T> elements) {
        if (collection == null || elements == null) {
            return false;
        }
        return elements.stream().anyMatch(collection::contains);
    }

    public static <T> boolean containsAll(Collection<T> collection, Collection<T> elements) {
        if (collection == null || elements == null) {
            return false;
        }
        return collection.containsAll(elements);
    }

    public static <T> int frequency(Collection<T> collection, T element) {
        if (collection == null) {
            return 0;
        }
        return Collections.frequency(collection, element);
    }

    public static <T> Map<T, Integer> frequencies(Collection<T> collection) {
        if (collection == null) {
            return new HashMap<>();
        }
        return collection.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum));
    }

    public static <T> T mostFrequent(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return frequencies(collection).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static <T> List<T> rotate(List<T> list, int distance) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> rotated = new ArrayList<>(list);
        Collections.rotate(rotated, distance);
        return rotated;
    }

    public static <T> List<T> fill(int size, T value) {
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(value);
        }
        return list;
    }

    public static <T> List<T> fillWith(int size, Supplier<T> supplier) {
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    public static <T> List<T> range(int start, int end, Function<Integer, T> mapper) {
        List<T> list = new ArrayList<>();
        for (int i = start; i < end; i++) {
            list.add(mapper.apply(i));
        }
        return list;
    }

    public static <T extends Comparable<T>> List<T> sorted(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().sorted().collect(Collectors.toList());
    }

    public static <T> List<T> sorted(List<T> list, Comparator<T> comparator) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static <T extends Comparable<T>> T min(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return Collections.min(collection);
    }

    public static <T> T min(Collection<T> collection, Comparator<T> comparator) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return Collections.min(collection, comparator);
    }

    public static <T extends Comparable<T>> T max(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return Collections.max(collection);
    }

    public static <T> T max(Collection<T> collection, Comparator<T> comparator) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return Collections.max(collection, comparator);
    }

    public static <T> boolean allMatch(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return true;
        }
        return list.stream().allMatch(predicate);
    }

    public static <T> boolean anyMatch(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    public static <T> boolean noneMatch(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return true;
        }
        return list.stream().noneMatch(predicate);
    }

    public static <T> long count(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return 0;
        }
        return list.stream().filter(predicate).count();
    }

    public static <T> Optional<T> findFirst(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return Optional.empty();
        }
        return list.stream().filter(predicate).findFirst();
    }

    public static <T> Optional<T> findLast(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return Optional.empty();
        }
        List<T> filtered = filter(list, predicate);
        return filtered.isEmpty() ? Optional.empty() : Optional.of(filtered.get(filtered.size() - 1));
    }

    public static <T> int indexOf(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int lastIndexOf(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return -1;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> result = new HashMap<>();
        if (map1 != null) result.putAll(map1);
        if (map2 != null) result.putAll(map2);
        return result;
    }

    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2, BiFunction<V, V, V> mergeFunction) {
        Map<K, V> result = new HashMap<>();
        if (map1 != null) result.putAll(map1);
        if (map2 != null) {
            map2.forEach((key, value) -> result.merge(key, value, mergeFunction));
        }
        return result;
    }

    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (v1, v2) -> v1));
    }

    public static <K, V> List<Map.Entry<K, V>> sortByKey(Map<K, V> map) {
        if (map == null) {
            return new ArrayList<>();
        }
        return map.entrySet().stream()
                .sorted((Comparator<? super Map.Entry<K, V>>) Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public static <K, V> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        if (map == null) {
            return new ArrayList<>();
        }
        return map.entrySet().stream()
                .sorted((Comparator<? super Map.Entry<K, V>>) Map.Entry.comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    public static <T> List<T> safeList(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }

    public static <T> Set<T> safeSet(Set<T> set) {
        return set != null ? set : new HashSet<>();
    }

    public static <K, V> Map<K, V> safeMap(Map<K, V> map) {
        return map != null ? map : new HashMap<>();
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("Safe get index 2: " + safeGet(numbers, 2));
        System.out.println("Safe get index 100: " + safeGet(numbers, 100, -1));

        List<Integer> evens = filter(numbers, n -> n % 2 == 0);
        System.out.println("Even numbers: " + evens);

        List<List<Integer>> partitions = partition(numbers, 3);
        System.out.println("Partitions of size 3: " + partitions);

        Map<Boolean, List<Integer>> partitioned = partitionBy(numbers, n -> n > 5);
        System.out.println("Numbers > 5: " + partitioned.get(true));

        Map<Integer, Long> freq = frequencies(numbers).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
        System.out.println("Frequencies: " + freq);

        List<Integer> union = union(Arrays.asList(1, 2, 3), Arrays.asList(3, 4, 5));
        System.out.println("Union: " + union);
    }
}
