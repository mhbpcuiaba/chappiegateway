package com.chappiegateway.upstreams.nettymock;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.Map;

public class RoutingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Map<String, RouteHandler> routes = Map.of(
            "/hello", new HelloHandler()
    );
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        String path = new QueryStringDecoder(request.uri()).path();
        RouteHandler routeHandler = routes.get(path);

        FullHttpResponse response;

        if (routeHandler != null) {
            response = routeHandler.handle(request);
        } else {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND
            );
        }

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }
}
