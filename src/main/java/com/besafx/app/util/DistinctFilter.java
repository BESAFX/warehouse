package com.besafx.app.util;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class DistinctFilter {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
    }
}
