package com.chappiegateway.core.server;


import com.chappiegateway.core.execution.AsyncGatewayHandler;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.Headers;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class NettyGatewayAdapterTest {

    @Test
    void shouldReturnResponseFromGatewayHandler() {

        AsyncGatewayHandler handler =  (RequestContext ctx, InboundRequest request) -> {

            byte[] body = "test-response".getBytes(StandardCharsets.UTF_8);

            Headers headers = DefaultHeaders.empty()
                    .with("content-type", "text/plain");

            OutboundResponse response =
                    OutboundResponse.of(200, headers, body);

            return CompletableFuture.completedFuture(response);
        };

        EmbeddedChannel channel =
                new EmbeddedChannel(new NettyGatewayAdapter(handler));

        FullHttpRequest request =
                new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1,
                        HttpMethod.GET,
                        "/hello",
                        Unpooled.EMPTY_BUFFER
                );

        request.headers().set(HttpHeaderNames.HOST, "localhost");

        channel.writeInbound(request);

        FullHttpResponse response = channel.readOutbound();

        assertEquals(200, response.status().code());

        String body =
                response.content().toString(StandardCharsets.UTF_8);

        assertEquals("test-response", body);
    }
}