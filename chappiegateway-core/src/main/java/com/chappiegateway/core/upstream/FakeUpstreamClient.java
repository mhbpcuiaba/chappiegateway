package com.chappiegateway.core.upstream;


import com.chappiegateway.core.model.DefaultHeaders;

public final class FakeUpstreamClient implements UpstreamClient {

    @Override
    public UpstreamResponse execute(UpstreamRequest request) {
        return new UpstreamResponse(
                200,
                DefaultHeaders.empty(),
                "OK".getBytes()
        );
    }
}
