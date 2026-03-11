package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.*;
import com.chappiegateway.core.model.*;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingFilterTest {

    @Test
    void shouldForwardRequestToNextFilter() throws Exception {

        LoggingFilter filter = new LoggingFilter();

        OutboundResponse expectedResponse =
                OutboundResponse.of(
                        200,
                        DefaultHeaders.empty(),
                        "OK".getBytes()
                );

        AsyncFilterChain chain = (ctx, request) -> {
            assertThat(request.path()).isEqualTo("/hello");
            return CompletableFuture.completedFuture(expectedResponse);
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

        assertThat(response).isEqualTo(expectedResponse);
    }
}