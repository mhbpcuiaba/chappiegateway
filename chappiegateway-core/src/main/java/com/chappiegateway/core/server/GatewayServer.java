package com.chappiegateway.core.server;

import com.chappiegateway.core.execution.AsyncGatewayHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class GatewayServer {

    private final int port;
    private final AsyncGatewayHandler handler;

    public GatewayServer(int port, AsyncGatewayHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() throws InterruptedException {


        try (NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
             NioEventLoopGroup workerGroup = new NioEventLoopGroup()) {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();

                            channelPipeline.addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(1024 * 1024))
                                    .addLast(new NettyGatewayAdapter(handler));
                        }
                    });

            Channel channel = serverBootstrap.bind(port).sync().channel();

            System.out.println("ChappieGateway running on port " + port);

            channel.closeFuture().sync();
        }
    }
}
