package com.chappiegateway.core.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthHandlerTest {

    @Test
    void shouldReturn200AndHealthMessage() {
        HealthHandler handler = new HealthHandler();

        FullHttpResponse resp = handler.handle((FullHttpRequest) null);

        assertThat(resp.status()).isEqualTo(HttpResponseStatus.OK);
        String body = resp.content().toString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).contains("Chappie Gateway running fine.");
    }
}
