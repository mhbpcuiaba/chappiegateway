package com.chappiegateway.core.upstream;

import com.chappiegateway.core.model.Headers;

import java.util.Optional;

public record UpstreamResponse(
        int status,
        Headers headers,
        byte[] body
) {
    public Optional<byte[]> bodyOpt() {
        return Optional.ofNullable(body);
    }
}