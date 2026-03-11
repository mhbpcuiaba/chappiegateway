package com.chappiegateway.core.util;

import io.netty.buffer.ByteBuf;

public final class ByteBufUtils {

    public static byte[] toByteArray(ByteBuf buf) {
        if (buf == null) {
            return null;
        }

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

}