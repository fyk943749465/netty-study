package com.feng.netty.c2.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugRead;

@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 配置未非阻塞模式，默认情况下是阻塞模式
        ssc.configureBlocking(false);
        // 服务器绑定端口
        ssc.bind(new InetSocketAddress(8080));

        // 存储 客户端的连接
        List<SocketChannel> channelList = new ArrayList<>();
        while (true) {
            SocketChannel sc = ssc.accept();  // 等待客户端建立连接，非阻塞模式下如果没有客户端连接返回null
            if (sc != null) {
                log.debug("Connected... {}", sc);
                sc.configureBlocking(false); // 客户端的连接也要配置非阻塞，防止被阻塞
                channelList.add(sc);
            }
            channelList.forEach(socketChannel -> {
                try {
                    int read = socketChannel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        debugRead(buffer); // 打印
                        buffer.clear();
                        log.debug("after read {}", socketChannel);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
