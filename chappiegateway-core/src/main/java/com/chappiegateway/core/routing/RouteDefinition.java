package com.chappiegateway.core.routing;

import java.net.URI;
import java.util.Objects;

public final class RouteDefinition {

    private final String id;
    private final String pathPattern;
    private final URI upstreamUri;

    public RouteDefinition(String id,
                           String pathPattern,
                           URI upstreamUri) {
        this.id = Objects.requireNonNull(id);
        this.pathPattern = Objects.requireNonNull(pathPattern);
        this.upstreamUri = Objects.requireNonNull(upstreamUri);
    }

    public String id() {
        return id;
    }

    public String pathPattern() {
        return pathPattern;
    }

    public URI upstreamUri() {
        return upstreamUri;
    }
}