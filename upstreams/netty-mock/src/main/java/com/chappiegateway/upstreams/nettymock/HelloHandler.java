package com.chappiegateway.upstreams.nettymock;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

public class HelloHandler implements RouteHandler{
    @Override
    public FullHttpResponse handle(FullHttpRequest request) {
        byte[] content = "Hello from upstream".getBytes();

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content));
    }
}
