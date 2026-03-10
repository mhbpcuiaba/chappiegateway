package com.chappiegateway.core.server;

import com.chappiegateway.core.execution.AsyncGatewayHandler;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.DefaultHeaders;
import com.chappiegateway.core.model.Headers;
import com.chappiegateway.core.model.InboundRequest;
import com.chappiegateway.core.model.OutboundResponse;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class GatewayServerTest {

    @Test
    void shouldStartServerAndHandleRequest() throws Exception {

        int port = 9090;

        AsyncGatewayHandler handler = (RequestContext ctx, InboundRequest request) -> {

            byte[] body = "gateway-test".getBytes(StandardCharsets.UTF_8);

            Headers headers = DefaultHeaders.empty()
                    .with("content-type", "text/plain");

            OutboundResponse response =
                    OutboundResponse.of(200, headers, body);

            return CompletableFuture.completedFuture(response);
        };

        GatewayServer server = new GatewayServer(port, handler);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (InterruptedException ignored) {
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();

        Thread.sleep(500); // allow server to start

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/hello"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("gateway-test", response.body());
    }
}
