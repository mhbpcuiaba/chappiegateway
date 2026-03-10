package com.chappiegateway.core.model;

import java.util.List;
import java.util.Optional;

public interface Headers {
    Optional<String> first(String name);
    List<String> all(String name);
    boolean contains(String name);
    Headers with(String name, String value);

}