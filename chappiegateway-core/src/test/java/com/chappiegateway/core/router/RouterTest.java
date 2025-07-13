package com.chappiegateway.core.router;

import com.chappiegateway.core.handler.HealthHandler;
import com.chappiegateway.core.handler.RouteHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RouterTest {

    @Test
    void shouldReturnCorrectHandler() {
        Router router = Router.builder()
                .get("/health", new HealthHandler())
                .build();

        FullHttpRequest req = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, "/health"
        );

        RouteHandler handler = router.findHandler(req);

        assertThat(handler).isInstanceOf(HealthHandler.class);
    }

    @Test
    void shouldReturnNotFoundHandlerForUnknownRoute() {
        Router router = Router.builder()
                .get("/health", new HealthHandler())
                .build();

        FullHttpRequest req = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, "/unknown"
        );

        RouteHandler handler = router.findHandler(req);

        assertThat(handler.getClass().getSimpleName()).isEqualTo("NotFoundHandler");
    }
}
