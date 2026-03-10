package com.chappiegateway.core.execution;


import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import java.util.concurrent.CompletionStage;

public interface AsyncFilterChain {
    CompletionStage<OutboundResponse> proceed(RequestContext ctx, InboundRequest request);
}
