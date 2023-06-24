package com.feng.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {

    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());// 这里分配的内存，不能再动态调整
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /**
         * class java.nio.HeapByteBuffer   堆内存，效率相对低，
         * class java.nio.DirectByteBuffer 直接内存
         */
    }
}
