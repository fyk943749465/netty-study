package com.feng.netty.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFiledDecoder {

    public static void main(String[] args) {

        EmbeddedChannel channel =new EmbeddedChannel(
                // 消息的最大长度，长度字段偏移量，长度字段的长度，是否需要调整，解析的内容是否需要剥离
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 4 个字节的长度，实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        send(buffer, "Hello, world");
        send(buffer, "Hi");
        channel.writeInbound(buffer);

    }

    private static void send(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes();
        int length = bytes.length;

        buffer.writeInt(length);  // 内容长度
        buffer.writeBytes(bytes); // 实际内容
    }

}
