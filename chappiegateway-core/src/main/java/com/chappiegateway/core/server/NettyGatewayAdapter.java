package com.chappiegateway.core.server;

import com.chappiegateway.core.execution.AsyncGatewayHandler;
import com.chappiegateway.core.execution.CancellationToken;
import com.chappiegateway.core.execution.RequestContext;
import com.chappiegateway.core.model.Attributes;
import com.chappiegateway.core.model.HttpMethod;
import com.chappiegateway.core.model.InboundRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import com.chappiegateway.core.model.*;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.netty.handler.codec.http.*;


public class NettyGatewayAdapter extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final AsyncGatewayHandler gatewayHandler;
    public NettyGatewayAdapter(AsyncGatewayHandler gatewayHandler) {
        this.gatewayHandler = gatewayHandler;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        InboundRequest inbound = mapRequest(req, ctx);

        RequestContext requestContext =
                new RequestContext(
                        Clock.systemUTC(),
                        Instant.now().plusSeconds(30),
                        Executors.newSingleThreadScheduledExecutor(),
                        new CancellationToken()
                );

        gatewayHandler.handle(requestContext, inbound)
                .whenComplete((response, error) -> {

                    if (error != null) {
                        sendError(ctx);
                        return;
                    }

                    writeResponse(ctx, response);
                });
    }


    private InboundRequest mapRequest(FullHttpRequest req, ChannelHandlerContext ctx) {

        byte[] body = new byte[req.content().readableBytes()];
        req.content().readBytes(body);

        return new InboundRequest(
                UUID.randomUUID().toString(),
                HttpMethod.valueOf(req.method().name()),
                "http",
                req.headers().get(HttpHeaderNames.HOST),
                req.uri(),
                Optional.empty(),
                DefaultHeaders.fromNetty(req.headers()),
                body.length == 0 ? Optional.empty() : Optional.of(body),
                Optional.of(ctx.channel().remoteAddress().toString()),
                Attributes.empty()
        );
    }

    private void writeResponse(ChannelHandlerContext ctx, OutboundResponse response) {

        byte[] body = response.body().orElse(new byte[0]);

        FullHttpResponse nettyResponse =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.valueOf(response.status()),
                        Unpooled.wrappedBuffer(body)
                );

        nettyResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length);

        ctx.writeAndFlush(nettyResponse);
    }

    private void sendError(ChannelHandlerContext ctx) {

        FullHttpResponse res =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.INTERNAL_SERVER_ERROR
                );

        ctx.writeAndFlush(res);
    }
}
