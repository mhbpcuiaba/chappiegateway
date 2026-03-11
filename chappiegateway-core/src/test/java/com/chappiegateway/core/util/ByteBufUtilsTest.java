package com.chappiegateway.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ByteBufUtilsTest {

    @Test
    void shouldReturnNullWhenBufferIsNull() {

        byte[] result = ByteBufUtils.toByteArray(null);

        assertThat(result).isNull();
    }

    @Test
    void shouldConvertByteBufToByteArray() {

        ByteBuf buf = Unpooled.wrappedBuffer("hello".getBytes());

        byte[] result = ByteBufUtils.toByteArray(buf);

        assertThat(result).isEqualTo("hello".getBytes());
    }

    @Test
    void shouldConsumeReadableBytes() {

        ByteBuf buf = Unpooled.wrappedBuffer("abc".getBytes());

        ByteBufUtils.toByteArray(buf);

        assertThat(buf.readableBytes()).isEqualTo(0);
    }
}