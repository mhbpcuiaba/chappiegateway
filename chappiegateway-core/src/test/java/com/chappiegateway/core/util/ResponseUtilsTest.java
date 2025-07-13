package com.chappiegateway.core.util;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseUtilsTest {

    @Test
    void shouldBuildResponseCorrectly() {
        String content = "test response";
        FullHttpResponse resp = ResponseUtils.buildResponse(HttpResponseStatus.OK, content, "text/plain");

        assertThat(resp.status()).isEqualTo(HttpResponseStatus.OK);
        assertThat(resp.headers().get("Content-Type")).isEqualTo("text/plain");

        String body = resp.content().toString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(body).isEqualTo(content);
    }
}
