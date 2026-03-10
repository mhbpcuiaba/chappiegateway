package com.chappiegateway.core.model;

import java.util.Map;
import java.util.Optional;

public final class Attributes {
    private final Map<String, Object> data;

    public Attributes(Map<String, Object> data) {
        this.data = Map.copyOf(data);
    }

    public Map<String, Object> asMap() { return data; }

    public <T> Optional<T> get(String key, Class<T> type) {
        Object v = data.get(key);
        if (type.isInstance(v)) return Optional.of(type.cast(v));
        return Optional.empty();
    }

    public Attributes with(String key, Object value) {
        var copy = new java.util.HashMap<>(data);
        copy.put(key, value);
        return new Attributes(copy);
    }

    public static Attributes empty() {
        return new Attributes(Map.of());
    }
}
