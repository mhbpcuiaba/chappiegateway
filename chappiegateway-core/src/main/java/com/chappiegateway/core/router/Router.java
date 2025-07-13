package com.chappiegateway.core.router;

import com.chappiegateway.core.handler.NotFoundHandler;
import com.chappiegateway.core.handler.RouteHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Router {
    private final Map<String, RouteHandler> routeMap;

    private Router(Map<String, RouteHandler> routeMap) {
        this.routeMap = routeMap;
    }

    public RouteHandler findHandler(FullHttpRequest req) {
        String key = req.method().name() + ":" + req.uri();
        return routeMap.getOrDefault(key, new NotFoundHandler());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, RouteHandler> routeMap = new ConcurrentHashMap<>();

        public Builder get(String path, RouteHandler handler) {
            routeMap.put("GET:" + path, handler);
            return this;
        }

        public Builder post(String path, RouteHandler handler) {
            routeMap.put("POST:" + path, handler);
            return this;
        }

        public Builder route(Route route, RouteHandler handler) {
            String key = route.getMethod().name() + ":" + route.getPath();
            routeMap.put(key, handler);
            return this;
        }

        public Router build() {
            return new Router(routeMap);
        }
    }
}