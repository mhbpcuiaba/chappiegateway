package com.chappiegateway.core;

import com.chappiegateway.core.execution.*;
import com.chappiegateway.core.execution.filter.AsyncFilter;
import com.chappiegateway.core.execution.filter.LoggingFilter;
import com.chappiegateway.core.execution.filter.RoutingFilter;
import com.chappiegateway.core.routing.*;
import com.chappiegateway.core.server.GatewayServer;
import com.chappiegateway.core.upstream.NettyAsyncUpstreamClient;
import com.chappiegateway.core.util.Banner;
import java.util.List;

public class GatewayServerApp {

    private static final int PORT = 8085;

    public static void main(String[] args) throws InterruptedException {
        Banner.printBanner(PORT);

        AsyncGatewayHandler gatewayHandler = buildGateway();

        new GatewayServer(PORT, gatewayHandler).start();
    }

    private static AsyncGatewayHandler buildGateway() {

        Router router = new DefaultRouter(new RouteLoader().load());

        AsyncTerminalHandler terminalHandler =
                new AsyncUpstreamTerminalHandler(new NettyAsyncUpstreamClient());

        List<AsyncFilter> filters = List.of(
                new LoggingFilter(),
                new RoutingFilter(router)
        );;

        AsyncFilterChain filterChain =
                new DefaultAsyncFilterChain(filters, terminalHandler);

        return filterChain::proceed;
    }
}