package com.chappiegateway.core.upstream;

import com.chappiegateway.core.execution.RequestContext;

import java.util.concurrent.CompletionStage;

public interface AsyncUpstreamClient {
    CompletionStage<UpstreamResponse> execute(RequestContext ctx, UpstreamRequest request);
}