package com.chappiegateway.core.upstream;

import com.chappiegateway.core.model.Headers;
import io.netty.buffer.ByteBuf;

import java.util.Optional;

public record UpstreamResponse(
        int status,
        Headers headers,
        ByteBuf body
) {
    public Optional<ByteBuf> bodyOpt() {
        return Optional.ofNullable(body);
    }
}