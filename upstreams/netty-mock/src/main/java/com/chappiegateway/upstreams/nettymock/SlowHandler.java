package com.chappiegateway.upstreams.nettymock;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

public class SlowHandler implements RouteHandler {

    private static final int MAX_DELAY_SECONDS = 5;

    @Override
    public FullHttpResponse handle(FullHttpRequest request) {

        int delay = getDelay(request);

        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        byte[] content =
                "slow from upstream".getBytes(StandardCharsets.UTF_8);

        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content)
        );
    }

    private int getDelay(FullHttpRequest request) {

        QueryStringDecoder decoder =
                new QueryStringDecoder(request.uri());

        List<String> values =
                decoder.parameters().get("delay");

        if (values == null || values.isEmpty()) {
            return randomDelay();
        }

        try {
            int delay = Integer.parseInt(values.getFirst());

            if (delay > 0 && delay <= MAX_DELAY_SECONDS) {
                return delay;
            }
        } catch (NumberFormatException ignored) {
        }

        return randomDelay();
    }

    private int randomDelay() {
        return RandomGenerator
                .getDefault()
                .nextInt(1, MAX_DELAY_SECONDS + 1);
    }
}