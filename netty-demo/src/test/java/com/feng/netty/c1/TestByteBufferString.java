package com.feng.netty.c1;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

public class TestByteBufferString {

    /**
     * 三种将字符串转换成 ByteBuffer 的方式
     *
     * @param args
     */
    public static void main(String[] args) {
        // 1. 字符串 和 bytebuffer 之间的转换
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());  // 写入完成之后，buffer 还处于写模式
        debugAll(buffer);

        // 2. charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello"); // 该方式写入数据后，buffer1 处于了读模式下
        debugAll(buffer1);

        // 3. wrap
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer2);

        // 将 ByteBuffer 转换成 字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str1);

    }
}
