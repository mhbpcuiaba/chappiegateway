package com.chappiegateway.upstreams.nettymock;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface RouteHandler {
    FullHttpResponse handle(FullHttpRequest request);
}