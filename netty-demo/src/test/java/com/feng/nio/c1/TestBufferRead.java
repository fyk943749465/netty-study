package com.feng.nio.c1;

import java.nio.ByteBuffer;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

public class TestBufferRead {

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // rewind 从头开始读
        debugAll(buffer);
        buffer.rewind(); // 丛头开始读
        debugAll(buffer);
        System.out.println((char)buffer.get());

        // mark & reset
        // mark 做一个标记，记录 position 的位置， reset 是将 position 重置到 mark 的位置
        System.out.println((char) buffer.get());
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset(); // 重置回到 mark 的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        System.out.println((char) buffer.get(3));  // get(i) 方法，不会改变 position 的值
        debugAll(buffer);
    }
}
