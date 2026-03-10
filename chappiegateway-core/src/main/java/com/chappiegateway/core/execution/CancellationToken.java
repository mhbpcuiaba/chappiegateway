package com.chappiegateway.core.execution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CancellationToken {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final CompletableFuture<Void> cancelledFuture = new CompletableFuture<>();

    public boolean cancel() {
        boolean did = cancelled.compareAndSet(false, true);
        if (did) cancelledFuture.complete(null);
        return did;
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    /** Completes when cancelled. */
    public CompletionStage<Void> cancelled() {
        return cancelledFuture;
    }
}
