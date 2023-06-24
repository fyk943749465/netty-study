package com.feng.nio.c1;



import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // 1. 输入流， 2， RandomAccessFile
        try (FileChannel channel = new FileInputStream("netty-demo/data.txt").getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(buffer);
                if (len == -1) {
                    break;
                }
                log.debug("读取到的字节数{}", len);
                buffer.flip(); // 切换读模式
                while (buffer.hasRemaining()) { // 是否还有剩余数据
                    byte b = buffer.get();
                    log.debug("读取到的字节{}", (char)b);
                }
                buffer.clear(); // 切换到写模式
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
