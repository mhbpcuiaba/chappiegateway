package com.chappiegateway.core.model;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.*;

public final class DefaultHeaders implements Headers {

    private final Map<String, List<String>> values;

    private DefaultHeaders(Map<String, List<String>> values) {
        this.values = values;
    }

    public static Headers empty() {
        return new DefaultHeaders(Map.of());
    }

    public static Headers of(Map<String, List<String>> values) {
        return new DefaultHeaders(Map.copyOf(values));
    }

    @Override
    public Optional<String> first(String name) {
        List<String> v = values.get(name.toLowerCase());
        return (v == null || v.isEmpty()) ? Optional.empty() : Optional.of(v.get(0));
    }

    @Override
    public List<String> all(String name) {
        return values.getOrDefault(name.toLowerCase(), List.of());
    }

    @Override
    public boolean contains(String name) {
        return values.containsKey(name.toLowerCase());
    }

    public Headers with(String name, String value) {
        Map<String, List<String>> copy = new HashMap<>(values);
        copy.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(value);
        return new DefaultHeaders(copy);
    }

    public static Headers fromNetty(HttpHeaders nettyHeaders) {

        Map<String, List<String>> map = new HashMap<>();

        for (Map.Entry<String, String> entry : nettyHeaders) {
            String name = entry.getKey().toLowerCase();
            String value = entry.getValue();

            map.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        }

        return new DefaultHeaders(Map.copyOf(map));
    }
}
