package com.feng.nio.c1;

import java.nio.ByteBuffer;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)0x61);
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        debugAll(buffer);

        buffer.flip();
        System.out.println(buffer.get());
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x66, 0x67});
        debugAll(buffer);
    }
}
