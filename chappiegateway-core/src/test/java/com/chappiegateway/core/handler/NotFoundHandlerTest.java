package com.chappiegateway.core.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundHandlerTest {

    @Test
    void shouldReturn404() {
        NotFoundHandler handler = new NotFoundHandler();

        FullHttpResponse resp = handler.handle((FullHttpRequest) null);

        assertThat(resp.status()).isEqualTo(HttpResponseStatus.NOT_FOUND);
        String body = resp.content().toString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).contains("404 Not Found");
    }
}
