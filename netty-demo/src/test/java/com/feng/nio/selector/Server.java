package com.feng.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

@Slf4j
public class Server {

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
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 将 sc 与 selector 关联，并且当前的sc 关联一个特定的 buffer 缓冲区，每个与服务端建立的socket 都有自己的一个 buffer 缓冲区
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ); // 在 sc 上注册刚兴趣的事件
                } else if (key.isReadable()) {
                    // 捕获异常
                    try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = socketChannel.read(buffer);
                        if (read == -1) {
                            // 正常断开，这个事件也需要处理
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) { // 表示buffer缓冲区不够，一条完整消息在split 中没有读完
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);//对原来的buffer进行扩容
                                buffer.flip(); // 切换到读模式
                                newBuffer.put(buffer);  // 将内容复制到新的扩容后的 buffer 中
                                key.attach(newBuffer); // selectorKey 上关联新的 buffer
                            }
                        }
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
