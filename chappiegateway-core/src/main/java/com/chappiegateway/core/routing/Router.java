package com.chappiegateway.core.routing;

import com.chappiegateway.core.model.InboundRequest;

import java.util.Optional;

public interface Router {
    Optional<RouteMatch> route(InboundRequest request);
}
