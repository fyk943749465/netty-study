package com.feng.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.feng.netty.bytebuf.TestByteBuf.log;

/**
 * slice方法，零拷贝的一个应用
 */
public class TestSlice {

    public static void main(String[] args) {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buffer);

        // 在切片过程中，没有发生数据复制
        ByteBuf buf1 = buffer.slice(0, 5);  // 切片后的结果，不允许写入更多的内容。（因为用的是原来的buffer的内存）
        ByteBuf buf2 = buffer.slice(5, 5);
        log(buf1);
        log(buf2);

        // 证明 slice 方法没有发生数据复制
        buf1.setByte(0, 'b');

        log(buffer);
        log(buf1);

        // 原始的 buffer 调用了 release 方法后，内存是否后，切片后的 ByteBuf 将不能再使用。
    }
}
