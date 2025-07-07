package com.chappiegateway.core;

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
                    .childHandler(new HttpServerInitializer())
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

    public void stop() {
        log.info("Shutting down gracefully...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline channelPipeline = socketChannel.pipeline();
            channelPipeline.addLast(new HttpServerCodec());
            channelPipeline.addLast(new HttpObjectAggregator(65536));
            channelPipeline.addLast(new RoutingHandler());
        }
    }

    static class RoutingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
            var uri = fullHttpRequest.uri();
            log.info("Received request: {} {}", fullHttpRequest.method(), uri);
            var defaultFullHttpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    channelHandlerContext.alloc().buffer().writeBytes("Hello from ChappieGateway!".getBytes())
            );

            defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            defaultFullHttpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, defaultFullHttpResponse.content().readableBytes());

            if (HttpUtil.isKeepAlive(fullHttpRequest)) {
                defaultFullHttpResponse.headers().set(HttpHeaderNames.CONNECTION,  HttpHeaderValues.KEEP_ALIVE);
                channelHandlerContext.writeAndFlush(defaultFullHttpResponse);
            } else {
                channelHandlerContext.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("Pipeline error", cause);
            ctx.close();
        }
    }

}
