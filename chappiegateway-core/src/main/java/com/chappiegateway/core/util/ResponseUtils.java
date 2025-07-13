package com.chappiegateway.core.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class ResponseUtils {
    public static FullHttpResponse buildResponse(HttpResponseStatus status, String content, String contentType) {
        var buf = Unpooled.copiedBuffer(content, StandardCharsets.UTF_8);
        var resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        return resp;
    }
}
