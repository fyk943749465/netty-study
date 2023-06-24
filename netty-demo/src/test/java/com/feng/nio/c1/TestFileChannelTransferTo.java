package com.feng.nio.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {

    public static void main(String[] args) {

        try (
                FileChannel from = new FileInputStream("netty-demo/data.txt").getChannel();
                FileChannel to = new FileOutputStream("netty-demo/to.txt").getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化，传输大小有限制，一次最多传输 2g
            from.transferTo(0, from.size(), to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
