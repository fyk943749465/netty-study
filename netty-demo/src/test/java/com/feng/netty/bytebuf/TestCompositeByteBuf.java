package com.feng.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.feng.netty.bytebuf.TestByteBuf.log;

/**
 * 将小的 ByteBuf 合成一个 大的 ByteBuf，但是不发生数据复制
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true, buf1, buf2); // 注意要加 true 参数，调整写指针的位置
        log(buffer);
    }
}
