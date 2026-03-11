package com.chappiegateway.core.server;

import com.chappiegateway.core.execution.*;
import com.chappiegateway.core.execution.filter.AsyncFilter;
import com.chappiegateway.core.execution.filter.LoggingFilter;
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

    private static final int PORT = 8085;

    public static void main(String[] args) throws InterruptedException {

        printBanner();

        AsyncGatewayHandler gatewayHandler = buildGateway();

        new GatewayServer(PORT, gatewayHandler).start();
    }

    private static AsyncGatewayHandler buildGateway() {

        Router router = buildRouter();

        AsyncUpstreamClient upstreamClient = buildUpstreamClient();

        AsyncTerminalHandler terminalHandler =
                new AsyncUpstreamTerminalHandler(upstreamClient);

        List<AsyncFilter> filters = buildFilters(router);

        AsyncFilterChain filterChain =
                new DefaultAsyncFilterChain(filters, terminalHandler);

        return (ctx, request) -> filterChain.proceed(ctx, request);
    }

    private static Router buildRouter() {

        List<Route> routes = List.of(
                new Route(
                        "hello-route",
                        Set.of(HttpMethod.GET),
                        "/hello",
                        URI.create("http://localhost:9000")
                )
        );

        return new DefaultRouter(routes);
    }

    private static AsyncUpstreamClient buildUpstreamClient() {
        return new FakeAsyncUpstreamClient();
    }

    private static List<AsyncFilter> buildFilters(Router router) {

        return List.of(
                new LoggingFilter(),
                new RoutingFilter(router)
        );
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
        System.out.println("ChappieGateway starting on port " + PORT);
    }
}