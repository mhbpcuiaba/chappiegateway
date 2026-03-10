package com.chappiegateway.core.execution.filter;


import com.chappiegateway.core.execution.AsyncFilterChain;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import java.util.concurrent.CompletionStage;

public interface AsyncFilter {
    CompletionStage<OutboundResponse> doFilter(RequestContext ctx, InboundRequest request, AsyncFilterChain chain);
}
