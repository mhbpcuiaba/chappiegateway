package com.chappiegateway.core.execution;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;

public final class RequestContext {
    private final Clock clock;
    private final Instant deadline;
    private final ScheduledExecutorService scheduler;
    private final CancellationToken cancellation;

    public RequestContext(Clock clock, Instant deadline, ScheduledExecutorService scheduler, CancellationToken cancellation) {
        this.clock = Objects.requireNonNull(clock);
        this.deadline = Objects.requireNonNull(deadline);
        this.scheduler = Objects.requireNonNull(scheduler);
        this.cancellation = Objects.requireNonNull(cancellation);
    }

    public Clock clock() { return clock; }
    public Instant deadline() { return deadline; }
    public ScheduledExecutorService scheduler() { return scheduler; }
    public CancellationToken cancellation() { return cancellation; }

    public Duration timeLeft() {
        Instant now = clock.instant();
        Duration d = Duration.between(now, deadline);
        return d.isNegative() ? Duration.ZERO : d;
    }

    public boolean isExpired() {
        return !clock.instant().isBefore(deadline);
    }
}

