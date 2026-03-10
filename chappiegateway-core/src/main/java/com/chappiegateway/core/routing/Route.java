package com.chappiegateway.core.routing;


import com.chappiegateway.core.model.HttpMethod;

import java.net.URI;
import java.util.Set;

public record Route(
        String id,
        Set<HttpMethod> methods,
        String pathTemplate,
        URI upstreamBaseUri
) {}

