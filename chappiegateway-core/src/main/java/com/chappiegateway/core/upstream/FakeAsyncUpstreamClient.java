package com.chappiegateway.core.upstream;


import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class FakeAsyncUpstreamClient implements AsyncUpstreamClient {

    @Override
    public CompletionStage<UpstreamResponse> execute(RequestContext ctx, UpstreamRequest request) {
        if (ctx.cancellation().isCancelled()) {
            CompletableFuture<UpstreamResponse> cf = new CompletableFuture<>();
            cf.completeExceptionally(new CancellationException("request cancelled"));
            return cf;
        }
        return CompletableFuture.completedFuture(
                new UpstreamResponse(200, DefaultHeaders.empty(), "OK".getBytes())
        );
    }
}