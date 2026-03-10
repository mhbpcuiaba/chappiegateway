package com.chappiegateway.core.server;

import com.chappiegateway.core.DemoGatewayHandler;

public class GatewayServerRunner {
    public static void main(String[] args) throws InterruptedException {
        new GatewayServer(8085, new DemoGatewayHandler()).start();
    }
}
