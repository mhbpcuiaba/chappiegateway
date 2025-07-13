package com.chappiegateway.core.handler;

import com.chappiegateway.core.util.ResponseUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class AiInferHandler implements RouteHandler {
    @Override
    public FullHttpResponse handle(io.netty.handler.codec.http.FullHttpRequest req) {
        String json = """
            {
              "input": "hello",
              "response": "mocked response"
            }
            """;
        return ResponseUtils.buildResponse(HttpResponseStatus.OK, json, "application/json");
    }
}
