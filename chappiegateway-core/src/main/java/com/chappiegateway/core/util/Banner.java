package com.chappiegateway.core.util;

public class Banner {

    public static void printBanner(int port) {

        String banner = """
      ██████╗██╗  ██╗ █████╗ ██████╗ ██████╗ ██╗███████╗
     ██╔════╝██║  ██║██╔══██╗██╔══██╗██╔══██╗██║██╔════╝
     ██║     ███████║███████║██████╔╝██████╔╝██║█████╗
     ██║     ██╔══██║██╔══██║██╔═══╝ ██╔═══╝ ██║██╔══╝
     ╚██████╗██║  ██║██║  ██║██║     ██║     ██║███████
      ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝     ╚═╝╚══════╝
    """;

        System.out.println(banner);
        System.out.println("ChappieGateway starting on port " + port);
    }
}
