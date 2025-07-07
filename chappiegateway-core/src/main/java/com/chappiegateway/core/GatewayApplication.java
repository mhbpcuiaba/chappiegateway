package com.chappiegateway.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayApplication {


    private static final Logger log = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {
        printBanner();
        Config config = Config.load();

        log.info("Starting ChappieGateway…");

        GatewayServer server = new GatewayServer(config.host, config.port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received. Stopping ChappieGateway…");
            server.stop();
        }));

        server.start().handle((result, throwable) -> {
            if (throwable != null) {
                log.error("Failed to start ChappieGateway", throwable);
                System.exit(1);
            }
            return null;
        }).join();
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
