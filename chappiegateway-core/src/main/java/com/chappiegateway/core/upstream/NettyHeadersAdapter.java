package com.chappiegateway.core.upstream;

import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.Headers;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

public final class NettyHeadersAdapter {

    public static Headers from(HttpHeaders nettyHeaders) {

        Headers headers = DefaultHeaders.empty();

        for (Map.Entry<String, String> entry : nettyHeaders) {
            headers = headers.with(entry.getKey(), entry.getValue());
        }

        return headers;
    }
}