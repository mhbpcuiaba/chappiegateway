package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.AsyncFilterChain;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public final class LoggingFilter implements AsyncFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public CompletionStage<OutboundResponse> doFilter(
            RequestContext ctx,
            InboundRequest request,
            AsyncFilterChain chain) {

        log.info("{} {}", request.method(), request.path());

        return chain.proceed(ctx, request);
    }

}