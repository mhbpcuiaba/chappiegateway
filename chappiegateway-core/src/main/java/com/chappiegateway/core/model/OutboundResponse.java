package com.chappiegateway.core.model;

import java.util.Optional;

public record OutboundResponse(
        int status,
        Headers headers,
        Optional<byte[]> body,
        Attributes attributes
) {

    public static OutboundResponse of(
            int status,
            Headers headers,
            byte[] body
    ) {
        return new OutboundResponse(
                status,
                headers,
                Optional.ofNullable(body),
                Attributes.empty()
        );
    }

    public OutboundResponse empty(int status) {
        return new OutboundResponse(
                status,
                DefaultHeaders.empty(),
                Optional.empty(),
                Attributes.empty()
        );
    }

    public OutboundResponse withHeader(String name, String value) {
        return new OutboundResponse(
                status,
                headers.with(name, value),
                body,
                attributes
        );
    }

    public OutboundResponse withAttribute(String key, Object value) {
        return new OutboundResponse(
                status,
                headers,
                body,
                attributes.with(key, value)
        );
    }
}