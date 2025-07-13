package com.chappiegateway.core;

import org.apache.hc.client5.http.fluent.Request;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayServerTest {

    static ExecutorService executor;
    static int testPort = 8181;  // use a port unlikely to be busy
    static GatewayServer server;

    @BeforeAll
    static void startServer() {
        executor = Executors.newSingleThreadExecutor();
        server = new GatewayServer("127.0.0.1", testPort);

        executor.submit(() -> {
            server.start()
                    .exceptionally(t -> {
                        t.printStackTrace();
                        return null;
                    })
                    .join();
        });

        // give Netty some time to bind
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
    }

    @AfterAll
    static void stopServer() {
        server.stop();
        executor.shutdownNow();
    }

    @Test
    void respondsToHttpRequest() throws Exception {
        String body = Request.get("http://127.0.0.1:" + testPort + "/health")
                .execute()
                .returnContent()
                .asString();

        assertThat(body).isEqualTo("Chappie Gateway running fine.");
    }
}
