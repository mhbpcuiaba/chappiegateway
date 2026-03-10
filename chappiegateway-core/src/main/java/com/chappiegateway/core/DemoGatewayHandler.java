package com.chappiegateway.core;


import com.chappiegateway.core.execution.AsyncGatewayHandler;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.Headers;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DemoGatewayHandler implements AsyncGatewayHandler {

    @Override
    public CompletionStage<OutboundResponse> handle(
            RequestContext ctx,
            InboundRequest request) {

        byte[] body = "Hello from ChappieGateway".getBytes(StandardCharsets.UTF_8);

        Headers headers = DefaultHeaders.empty()
                .with("content-type", "text/plain");

        OutboundResponse response = OutboundResponse.of(
                200,
                headers,
                body
        );

        return CompletableFuture.completedFuture(response);
    }
}