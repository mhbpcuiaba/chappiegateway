package com.chappiegateway.core.execution.filter;

import com.chappiegateway.core.execution.AsyncFilterChain;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import java.time.Duration;
import java.util.concurrent.*;

public final class TimeoutFilter implements AsyncFilter {

    @Override
    public CompletionStage<OutboundResponse> doFilter(RequestContext ctx, InboundRequest request, AsyncFilterChain chain) {
        if (ctx.isExpired()) {
            ctx.cancellation().cancel();
            return CompletableFuture.completedFuture(timeoutResponse());
        }

        CompletableFuture<OutboundResponse> result = new CompletableFuture<>();

        // 1) Schedule timeout based on deadline
        Duration left = ctx.timeLeft();
        ScheduledFuture<?> timeoutTask = ctx.scheduler().schedule(() -> {
            ctx.cancellation().cancel();
            result.complete(timeoutResponse());
        }, left.toMillis(), TimeUnit.MILLISECONDS);

        // 2) Run the rest of the chain
        chain.proceed(ctx, request).whenComplete((res, err) -> {
            timeoutTask.cancel(false);
            if (err != null) result.completeExceptionally(err);
            else result.complete(res);
        });

        // 3) If cancelled by downstream (client disconnect), complete with 499-style or 504 (your choice)
        ctx.cancellation().cancelled().whenComplete((v, err) -> {
            // If already completed, no-op.
            // If you prefer: result.complete(cancelledResponse());
        });

        return result;
    }

    private OutboundResponse timeoutResponse() {
        return OutboundResponse.of(504, DefaultHeaders.empty(), "Gateway Timeout".getBytes());
    }
}
