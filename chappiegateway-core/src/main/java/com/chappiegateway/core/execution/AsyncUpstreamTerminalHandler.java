package com.chappiegateway.core.execution;

import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;
import com.chappiegateway.core.routing.RouteMatch;
import com.chappiegateway.core.upstream.AsyncUpstreamClient;
import com.chappiegateway.core.upstream.UpstreamRequest;

import java.net.URI;
import java.util.concurrent.CompletionStage;

public final class AsyncUpstreamTerminalHandler implements AsyncTerminalHandler {

    private final AsyncUpstreamClient upstream;

    public AsyncUpstreamTerminalHandler(AsyncUpstreamClient upstream) {
        this.upstream = upstream;
    }

    @Override
    public CompletionStage<OutboundResponse> handle(RequestContext ctx, InboundRequest request) {
        RouteMatch match = request.attributes()
                .get("routeMatch", RouteMatch.class)
                .orElseThrow(() -> new IllegalStateException("Missing RouteMatch"));

        URI uri = match.upstreamBaseUri().resolve(request.path());

        UpstreamRequest upstreamRequest = new UpstreamRequest(
                request.method(),
                uri,
                request.headers(),
                request.body()
        );

        return upstream.execute(ctx, upstreamRequest)
                .thenApply(upRes -> new OutboundResponse(
                        upRes.status(),
                        upRes.headers(),
                        upRes.bodyOpt(),
                        request.attributes()
                ));
    }
}
