package com.chappiegateway.core.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface RouteHandler {
    FullHttpResponse handle(FullHttpRequest req);
}
