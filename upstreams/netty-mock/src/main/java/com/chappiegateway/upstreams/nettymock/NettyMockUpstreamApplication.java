package com.chappiegateway.upstreams.nettymock;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class NettyMockUpstreamApplication {

    static Integer PORT = 9000;

    public static void main(String[] args) throws InterruptedException {


        try (var boss = new NioEventLoopGroup(1);
             var workers = new NioEventLoopGroup()) {

            var boostrap = new ServerBootstrap();
            boostrap.group(boss, workers)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyMockInitializer());

            Channel channel = boostrap.bind(PORT).sync().channel();
            System.out.println("Mock upstream running on http://localhost:" + PORT);

            channel.closeFuture().sync();
        }
    }
}
