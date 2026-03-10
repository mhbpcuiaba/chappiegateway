package com.chappiegateway.core.upstream;


import com.chappiegateway.core.model.Headers;
import com.chappiegateway.core.model.HttpMethod;

import java.net.URI;
import java.util.Optional;

public record UpstreamRequest(
        HttpMethod method,
        URI uri,
        Headers headers,
        Optional<byte[]> body
) {}