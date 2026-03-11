package com.chappiegateway.core.execution;

import com.chappiegateway.core.model.*;
import com.chappiegateway.core.routing.RouteMatch;
import com.chappiegateway.core.routing.RoutingAttributes;
import com.chappiegateway.core.upstream.AsyncUpstreamClient;
import com.chappiegateway.core.upstream.UpstreamRequest;
import com.chappiegateway.core.upstream.UpstreamResponse;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncUpstreamTerminalHandlerTest {

    @Test
    void shouldCallUpstreamAndReturnOutboundResponse() throws Exception {

        AsyncUpstreamClient upstream = new AsyncUpstreamClient() {
            @Override
            public CompletionStage<UpstreamResponse> execute(RequestContext ctx, UpstreamRequest request) {

                return CompletableFuture.completedFuture(
                        new UpstreamResponse(
                                200,
                                DefaultHeaders.empty(),
                                Unpooled.wrappedBuffer("OK".getBytes())
                        )
                );
            }
        };

        AsyncUpstreamTerminalHandler handler =
                new AsyncUpstreamTerminalHandler(upstream);

        RouteMatch routeMatch = new RouteMatch(
                "hello-route",
                URI.create("http://backend"),
                Map.of()
        );

        Attributes attributes =
                Attributes.empty().with(RoutingAttributes.ROUTE_MATCH, routeMatch);

        InboundRequest request = new InboundRequest(
                "req-1",
                HttpMethod.GET,
                "http",
                "localhost:8085",
                "/hello",
                Optional.empty(),
                DefaultHeaders.empty(),
                Optional.empty(),
                Optional.of("127.0.0.1"),
                attributes
        );

        RequestContext ctx = new RequestContext(
                Clock.systemUTC(),
                Instant.now().plusSeconds(10),
                Executors.newSingleThreadScheduledExecutor(),
                new CancellationToken()
        );

        OutboundResponse response =
                handler.handle(ctx, request)
                        .toCompletableFuture()
                        .get(2, TimeUnit.SECONDS);

        assertThat(response.status()).isEqualTo(200);
        assertThat(response.body()).isPresent();
        assertThat(new String(response.body().get())).isEqualTo("OK");
    }
}