package com.chappiegateway.core.server;


import com.chappiegateway.core.execution.*;
import com.chappiegateway.core.execution.filter.AsyncFilter;
import com.chappiegateway.core.execution.filter.RoutingFilter;
import com.chappiegateway.core.model.HttpMethod;
import com.chappiegateway.core.routing.DefaultRouter;
import com.chappiegateway.core.routing.Route;
import com.chappiegateway.core.routing.Router;
import com.chappiegateway.core.upstream.AsyncUpstreamClient;
import com.chappiegateway.core.upstream.FakeAsyncUpstreamClient;

import java.net.URI;
import java.util.List;
import java.util.Set;

public class GatewayServerRunner {

    public static void main(String[] args) throws InterruptedException {

        List<Route> routes = List.of(
                new Route(
                        "hello-route",
                        Set.of(HttpMethod.GET),
                        "/hello",
                        URI.create("http://localhost:9000")
                )
        );

        Router router = new DefaultRouter(routes);

        AsyncUpstreamClient upstream = new FakeAsyncUpstreamClient();

        AsyncTerminalHandler terminal =
                new AsyncUpstreamTerminalHandler(upstream);

        List<AsyncFilter> filters = List.of(
                new RoutingFilter(router)
        );

        AsyncFilterChain chain =
                new DefaultAsyncFilterChain(filters, terminal);

        AsyncGatewayHandler gatewayHandler =
                (ctx, request) -> chain.proceed(ctx, request);

        printBanner();
        new GatewayServer(8085, gatewayHandler).start();
    }


    private static void printBanner() {
        String banner = """
      ██████╗██╗  ██╗ █████╗ ██████╗ ██████╗ ██╗███████╗
     ██╔════╝██║  ██║██╔══██╗██╔══██╗██╔══██╗██║██╔════╝
     ██║     ███████║███████║██████╔╝██████╔╝██║█████╗
     ██║     ██╔══██║██╔══██║██╔═══╝ ██╔═══╝ ██║██╔══╝
     ╚██████╗██║  ██║██║  ██║██║     ██║     ██║███████
      ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝     ╚═╝╚══════╝
    """;
        System.out.println(banner);
    }
}

