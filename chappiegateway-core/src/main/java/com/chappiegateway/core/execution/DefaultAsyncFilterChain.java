package com.chappiegateway.core.execution;

import com.chappiegateway.core.execution.filter.AsyncFilter;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public final class DefaultAsyncFilterChain implements AsyncFilterChain {

    private final List<AsyncFilter> filters;
    private final AsyncTerminalHandler terminal;
    private final int index;

    public DefaultAsyncFilterChain(List<AsyncFilter> filters, AsyncTerminalHandler terminal) {
        this(filters, terminal, 0);
    }

    private DefaultAsyncFilterChain(List<AsyncFilter> filters, AsyncTerminalHandler terminal, int index) {
        this.filters = Objects.requireNonNull(filters);
        this.terminal = Objects.requireNonNull(terminal);
        this.index = index;
    }

    @Override
    public CompletionStage<OutboundResponse> proceed(RequestContext ctx, InboundRequest request) {
        if (index < filters.size()) {
            AsyncFilter next = filters.get(index);
            return next.doFilter(ctx, request, new DefaultAsyncFilterChain(filters, terminal, index + 1));
        }
        return terminal.handle(ctx, request);
    }
}
