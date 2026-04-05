package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.AsyncFilterChain;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;
import com.chappiegateway.core.routing.Router;
import com.chappiegateway.core.routing.RouteMatch;
import com.chappiegateway.core.routing.RoutingAttributes;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class RoutingFilter implements AsyncFilter {

    private final Router router;

    public RoutingFilter(Router router) {
        this.router = router;
    }

    @Override
    public CompletionStage<OutboundResponse> doFilter(RequestContext ctx, InboundRequest request, AsyncFilterChain chain) {
        System.out.println("RoutingFilter: " + request);
        Optional<RouteMatch> match = router.route(request);

        if (match.isEmpty()) {

            return CompletableFuture.completedFuture(
                    new OutboundResponse(
                            404,
                            DefaultHeaders.empty(),
                            Optional.of("Not Found".getBytes()),
                            request.attributes()
                    )
            );
        }

        InboundRequest updated =
                request.withAttribute(RoutingAttributes.ROUTE_MATCH, match.get());

        return chain.proceed(ctx, updated);

    }
}