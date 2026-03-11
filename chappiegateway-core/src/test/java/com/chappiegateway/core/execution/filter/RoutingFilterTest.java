package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.*;
import com.chappiegateway.core.model.*;
import com.chappiegateway.core.routing.*;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

class RoutingFilterTest {

    @Test
    void shouldAttachRouteMatchAndProceedChain() throws Exception {

        RouteMatch routeMatch = new RouteMatch(
                "hello-route",
                URI.create("http://backend"),
                Map.of()
        );

        Router router = request -> Optional.of(routeMatch);

        RoutingFilter filter = new RoutingFilter(router);

        AsyncFilterChain chain = (ctx, request) -> {

            Optional<RouteMatch> stored =
                    request.attributes().get(RoutingAttributes.ROUTE_MATCH, RouteMatch.class);

            assertThat(stored).isPresent();
            assertThat(stored.get()).isEqualTo(routeMatch);

            return CompletableFuture.completedFuture(
                    OutboundResponse.of(
                            200,
                            DefaultHeaders.empty(),
                            "OK".getBytes()
                    )
            );
        };

        InboundRequest request = new InboundRequest(
                "req-1",
                HttpMethod.GET,
                "http",
                "localhost",
                "/hello",
                Optional.empty(),
                DefaultHeaders.empty(),
                Optional.empty(),
                Optional.empty(),
                Attributes.empty()
        );

        RequestContext ctx = new RequestContext(
                Clock.systemUTC(),
                Instant.now().plusSeconds(10),
                Executors.newSingleThreadScheduledExecutor(),
                new CancellationToken()
        );

        OutboundResponse response =
                filter.doFilter(ctx, request, chain)
                        .toCompletableFuture()
                        .get(2, TimeUnit.SECONDS);

        assertThat(response.status()).isEqualTo(200);
    }


    @Test
    void shouldThrowWhenRouteNotFound() {

        Router router = request -> Optional.empty();

        RoutingFilter filter = new RoutingFilter(router);

        AsyncFilterChain chain = (ctx, req) -> CompletableFuture.completedFuture(null);

        InboundRequest request = new InboundRequest(
                "req-1",
                HttpMethod.GET,
                "http",
                "localhost",
                "/unknown",
                Optional.empty(),
                DefaultHeaders.empty(),
                Optional.empty(),
                Optional.empty(),
                Attributes.empty()
        );

        RequestContext ctx = new RequestContext(
                Clock.systemUTC(),
                Instant.now().plusSeconds(10),
                Executors.newSingleThreadScheduledExecutor(),
                new CancellationToken()
        );

        assertThatThrownBy(() ->
                filter.doFilter(ctx, request, chain)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No route found");
    }
}