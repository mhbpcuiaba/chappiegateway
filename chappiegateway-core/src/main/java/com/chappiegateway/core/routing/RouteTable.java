package com.chappiegateway.core.routing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RouteTable {

    private final List<RouteDefinition> routes;

    public RouteTable(List<RouteDefinition> routes) {
        this.routes = List.copyOf(routes);
    }

    public List<RouteDefinition> routes() {
        return routes;
    }

    public Optional<RouteMatch> match(String path) {

        for (RouteDefinition route : routes) {

            if (matches(route.pathPattern(), path)) {

                return Optional.of(
                        new RouteMatch(
                                route.id(),
                                route.upstreamUri(), Map.of()
                        )
                );
            }
        }

        return Optional.empty();
    }

    private boolean matches(String pattern, String path) {

        // simple prefix match for MVP
        if (pattern.endsWith("/**")) {

            String prefix =
                    pattern.substring(0, pattern.length() - 3);

            return path.startsWith(prefix);
        }

        return pattern.equals(path);
    }
}