package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.AsyncFilterChain;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;
import com.chappiegateway.core.routing.Router;
import com.chappiegateway.core.routing.RouteMatch;
import com.chappiegateway.core.routing.RoutingAttributes;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public final class RoutingFilter implements AsyncFilter {

    private final Router router;

    public RoutingFilter(Router router) {
        this.router = router;
    }

    @Override
    public CompletionStage<OutboundResponse> doFilter(RequestContext ctx, InboundRequest request, AsyncFilterChain chain) {

        Optional<RouteMatch> match = router.route(request);

        if (match.isEmpty()) {
            throw new IllegalStateException("No route found for " + request.path());
        }

        InboundRequest updated =
                request.withAttribute(RoutingAttributes.ROUTE_MATCH, match.get());

        return chain.proceed(ctx, updated);

    }
}