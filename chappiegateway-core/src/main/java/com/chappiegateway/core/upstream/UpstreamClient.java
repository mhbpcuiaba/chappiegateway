package com.chappiegateway.core.upstream;

public interface UpstreamClient {

    UpstreamResponse execute(UpstreamRequest request);
}
