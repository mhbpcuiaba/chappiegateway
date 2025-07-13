package com.chappiegateway.core;

import com.chappiegateway.core.handler.AiInferHandler;
import com.chappiegateway.core.handler.HealthHandler;
import com.chappiegateway.core.http.RoutingHandler;
import com.chappiegateway.core.router.Router;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class GatewayServer {

    private static final Logger log = LoggerFactory.getLogger(GatewayServer.class);

    private final String host;
    private final int port;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public GatewayServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public CompletableFuture<Void> start() {

        var completableFuture = new CompletableFuture<Void>();

        try {
            new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer(getBasicRouter()))
                    .bind(host, port)
                    .addListener((ChannelFutureListener) bindFuture -> {
                        if (bindFuture.isSuccess()) {
                            log.info("🚀 ChappieGateway started at http://{}:{}", host, port);
                            bindFuture.channel().closeFuture().addListener(cf -> stop());
                            completableFuture.complete(null);
                        } else {
                            log.error("Failed to bind to {}:{}", host, port, bindFuture.cause());
                            stop();
                            completableFuture.completeExceptionally(bindFuture.cause());
                        }
                    });

        } catch (Exception e) {
            log.error("Unexpected error during startup", e);
            stop();
            completableFuture.completeExceptionally(e);
        }
        return completableFuture;
    }

    private static Router getBasicRouter() {
        return Router.builder()
                .get("/health", new HealthHandler())
                .post("/ai/infer", new AiInferHandler())
                .build();
    }

    public void stop() {
        log.info("Shutting down gracefully...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        private final Router router;

        public HttpServerInitializer(Router router) {
            this.router = router;
        }
        @Override
        protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline channelPipeline = socketChannel.pipeline();
            channelPipeline.addLast(new HttpServerCodec());
            channelPipeline.addLast(new HttpObjectAggregator(65536));
            channelPipeline.addLast(new RoutingHandler(router));
        }
    }
}
