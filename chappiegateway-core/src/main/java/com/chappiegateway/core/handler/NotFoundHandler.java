package com.chappiegateway.core.handler;

import com.chappiegateway.core.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class NotFoundHandler implements RouteHandler {
    @Override
    public FullHttpResponse handle(io.netty.handler.codec.http.FullHttpRequest req) {
        return ResponseUtils.buildResponse(HttpResponseStatus.NOT_FOUND, "404 Not Found", "text/plain");
    }
}
