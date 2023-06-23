package com.feng.netty.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

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
                ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                SocketChannel sc = channel.accept();  // 客户端的channel
                log.debug("{}", sc);
            }
        }

    }
}
