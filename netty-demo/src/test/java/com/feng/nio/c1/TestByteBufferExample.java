package com.feng.nio.c1;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

public class TestByteBufferExample {

    /**
     * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
     *
     * Hello,world\n
     * I'm zhangsan\n
     * How are you?\n
     * 变成了下面的两个 byteBuffer (黏包，半包)
     *
     * Hello,world\nI'm zhangsan\nHo
     * w are you?\n
     *
     * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
     */
    public static void main(String[] args) {

        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {

        source.flip();
        for (int i = 0; i < source.limit(); ++i) {

            if (source.get(i) == '\n') { // 表示读到一条完整消息的末尾了
                int length = i + 1 - source.position();  // 得到消息的长度
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; ++j) {
                    target.put(source.get());  // 完整的消息存入buffer中
                }
                debugAll(target);
            }
        }
        source.compact(); // 回到起始位置，留下未读的内容
    }
}
