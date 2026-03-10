package com.chappiegateway.core.routing;


import com.chappiegateway.core.model.InboundRequest;

import java.util.List;
import java.util.Optional;

public final class DefaultRouter implements Router {

    private final List<Route> routes;

    public DefaultRouter(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public Optional<RouteMatch> route(InboundRequest request) {

        for (Route route : routes) {

            if (!route.methods().contains(request.method())) {
                continue;
            }

            PathTemplate template = new PathTemplate(route.pathTemplate());

            Optional<java.util.Map<String, String>> match =
                    template.match(request.path());

            if (match.isPresent()) {
                return Optional.of(new RouteMatch(
                        route.id(),
                        route.upstreamBaseUri(),
                        match.get()
                ));
            }
        }

        return Optional.empty();
    }
}
