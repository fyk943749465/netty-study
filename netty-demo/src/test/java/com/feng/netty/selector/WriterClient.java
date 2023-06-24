package com.feng.netty.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriterClient {


    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(8080));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        int count = 0;
        while (true) {
            int read = sc.read(byteBuffer);
            count += read;
            System.out.println(count);
            byteBuffer.clear();
        }
    }
}
