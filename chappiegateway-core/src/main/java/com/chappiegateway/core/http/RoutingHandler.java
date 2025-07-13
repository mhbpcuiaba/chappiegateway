package com.chappiegateway.core.http;

import com.chappiegateway.core.handler.RouteHandler;
import com.chappiegateway.core.router.Router;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(RoutingHandler.class);
    private final Router router;

    public RoutingHandler(Router router) {
        this.router = router;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        log.info("Received request: {} {}", req.method(), req.uri());
        RouteHandler handler = router.findHandler(req);
        FullHttpResponse resp = handler.handle(req);

        if (HttpUtil.isKeepAlive(req)) {
            resp.headers().set("Connection", "keep-alive");
            ctx.writeAndFlush(resp);
        } else {
            ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Pipeline error", cause);
        ctx.close();
    }
}
