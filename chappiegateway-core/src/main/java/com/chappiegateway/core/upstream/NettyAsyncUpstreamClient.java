package com.chappiegateway.core.upstream;

import com.chappiegateway.core.execution.RequestContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class NettyAsyncUpstreamClient implements AsyncUpstreamClient {

    private final EventLoopGroup group = new NioEventLoopGroup();

    @Override
    public CompletionStage<UpstreamResponse> execute(
            RequestContext requestContext,
            UpstreamRequest request) {

        CompletableFuture<UpstreamResponse> result = new CompletableFuture<>();

        URI uri = request.uri();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) {

                        ch.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 1024))
                                .addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {

                                    @Override
                                    protected void channelRead0(
                                            ChannelHandlerContext ctx,
                                            FullHttpResponse response) {

                                        ByteBuf body = response.content().retain();

                                        result.complete(
                                                new UpstreamResponse(
                                                        response.status().code(),
                                                        NettyHeadersAdapter.from(response.headers()),
                                                        body
                                                )
                                        );

                                        ctx.close();
                                    }

                                    @Override
                                    public void exceptionCaught(
                                            ChannelHandlerContext ctx,
                                            Throwable cause) {

                                        result.completeExceptionally(cause);
                                        ctx.close();
                                    }
                                });
                    }
                });

        bootstrap.connect(uri.getHost(), resolvePort(uri))
                .addListener((ChannelFutureListener) future -> {

                    if (!future.isSuccess()) {
                        result.completeExceptionally(future.cause());
                        return;
                    }

                    Channel channel = future.channel();

                    ByteBuf content =
                            request.body()
                                    .map(Unpooled::wrappedBuffer)
                                    .orElseGet(Unpooled::buffer);

                    String path =
                            uri.getRawPath() +
                                    (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");

                    FullHttpRequest nettyRequest =
                            new DefaultFullHttpRequest(
                                    HttpVersion.HTTP_1_1,
                                    io.netty.handler.codec.http.HttpMethod.valueOf(request.method().name()),
                                    path,
                                    content
                            );

                    nettyRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
                    nettyRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                    nettyRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

                    channel.writeAndFlush(nettyRequest);
                });

        return result;
    }

    private int resolvePort(URI uri) {

        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return "https".equals(uri.getScheme()) ? 443 : 80;
    }
}