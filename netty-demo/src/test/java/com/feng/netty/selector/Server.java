package com.feng.netty.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {

        // 1. 创建 selector 管理多个 channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        SelectionKey sscKey = ssc.register(selector, 0, null); // 用 SelectionKey 管理事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT); // 感兴趣的是连接事件
        log.debug("register key:{}", sscKey);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // select() 方法告诉我们，事件发生后，不能置之不理，必须处理。否则发生异常情况
            selector.select(); // 阻塞方法，等待事件发生。当事件发生了，不去处理时候，这个方法将不再阻塞
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();  // 发生感兴趣的事件集合
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                log.debug("Key: {}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();  // 客户端的channel
                    sc.configureBlocking(false);
                    log.debug("{}", sc);
                    SelectionKey scKey = sc.register(selector, 0, null); // 将 sc 与 selector 关联
                    scKey.interestOps(SelectionKey.OP_READ); // 在 sc 上注册刚兴趣的事件
                } else if (key.isReadable()) {
                    // 捕获异常
                    try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        socketChannel.read(buffer);
                        buffer.flip();
                        debugAll(buffer);
                    } catch (IOException e) {
                       e.printStackTrace();
                       key.cancel(); // 发生异常，将感兴趣的事件取消掉（也是一种处理方式），事件必须处理
                    }
                }
                iter.remove(); // 这行代码很关键，收到感兴趣的事件后，要从 selectedKeys 中删除，避免重复处理，因为selector不会自动删除
            }
        }
    }
}
