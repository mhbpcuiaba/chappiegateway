package com.chappiegateway.core.upstream;

import com.chappiegateway.core.execution.RequestContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.ReadTimeoutException;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class NettyAsyncUpstreamClient implements AsyncUpstreamClient {

    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int READ_TIMEOUT_SEC = 3;

    private static final int MAX_RETRIES = 1;
    private final EventLoopGroup group = new NioEventLoopGroup();

    @Override
    public CompletionStage<UpstreamResponse> execute(RequestContext requestContext, UpstreamRequest request) {
        //TODO why requestContext? remove?
        return executeWithRetry(request, 0);
    }

    private CompletionStage<UpstreamResponse> executeWithRetry(
            UpstreamRequest request, int retry) {

        CompletableFuture<UpstreamResponse> result = new CompletableFuture<>();

        URI uri = request.uri();

        Bootstrap bootstrap = new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .handler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) {

                        ch.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SEC))
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

                                        ctx.close();
                                        handleFailure(
                                                request,
                                                retry,
                                                result,
                                                cause
                                        );
                                    }
                                });
                    }
                });

        bootstrap.connect(uri.getHost(), resolvePort(uri))
                .addListener((ChannelFutureListener) future -> {

                    if (!future.isSuccess()) {
                        handleFailure(
                                request,
                                retry,
                                result,
                                future.cause()
                        );

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

    private void handleFailure(
            UpstreamRequest request,
            int retry,
            CompletableFuture<UpstreamResponse> result,
            Throwable cause
    ) {

        if (retry < MAX_RETRIES && isRetryable(cause)) {

            executeWithRetry(request, retry + 1)
                    .whenComplete((resp, err) -> {

                        if (err != null) {
                            result.completeExceptionally(err);
                        } else {
                            result.complete(resp);
                        }

                    });

            return;
        }

        result.complete(toErrorResponse(cause));
    }

    private boolean isRetryable(Throwable cause) {

        return cause instanceof ConnectException
                || cause instanceof ReadTimeoutException
                || cause instanceof java.util.concurrent.TimeoutException;
    }

    private UpstreamResponse toErrorResponse(Throwable cause) {

        int status;

        if (cause instanceof ReadTimeoutException) {
            status = 504; // Gateway Timeout
        } else {
            status = 502; // Bad Gateway
        }

        ByteBuf body =
                Unpooled.copiedBuffer(
                        ("Upstream error: " + cause.getMessage()).getBytes()
                );

        return new UpstreamResponse(
                status,
                NettyHeadersAdapter.from(new DefaultHttpHeaders()
                        .set(HttpHeaderNames.CONTENT_TYPE, "text/plain")),
                body
        );
    }
    private int resolvePort(URI uri) {

        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return "https".equals(uri.getScheme()) ? 443 : 80;
    }
}