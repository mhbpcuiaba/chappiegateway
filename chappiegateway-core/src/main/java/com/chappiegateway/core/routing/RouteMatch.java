package com.chappiegateway.core.routing;

import java.net.URI;
import java.util.Map;

public record RouteMatch(
        String routeId,
        URI upstreamBaseUri,
        Map<String, String> pathParams
) {}
