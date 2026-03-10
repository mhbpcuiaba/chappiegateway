package com.chappiegateway.core.model;

import java.util.Optional;

public record InboundRequest(
        String requestId,
        HttpMethod method,
        String scheme,
        String authority,     // host[:port]
        String path,          // raw path
        Optional<String> rawQuery,
        Headers headers,
        Optional<byte[]> body,
        Optional<String> remoteAddress,
        Attributes attributes
) {
    public InboundRequest withAttribute(String key, Object value) {
        return new InboundRequest(
                requestId, method, scheme, authority, path, rawQuery,
                headers, body, remoteAddress,
                attributes.with(key, value)
        );
    }

}