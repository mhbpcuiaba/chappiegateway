package com.chappiegateway.core.router;


import io.netty.handler.codec.http.HttpMethod;

public class Route {
    private final HttpMethod method;
    private final String path;

    private Route(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public static Route get(String path) {
        return new Route(HttpMethod.GET, path);
    }

    public static Route post(String path) {
        return new Route(HttpMethod.POST, path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}