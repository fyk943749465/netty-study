package com.feng.netty.selector;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriterServer2 {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 700000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer); // buffer 的内容可能一次写不完成，但可以先写一次
                    System.out.println(write);    // 返回每次写给客户端的 size
                    if (buffer.hasRemaining()) {  // 如果还有内容没有写完，可以让selector 关注这个socket 上的可写事件
                        // 先拿到原来关注的事件，在此基础上增加感兴趣的事件  SelectionKey.OP_WRITE
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        //scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        scKey.attach(buffer); // 把未写完的buffer挂载selectionKey上
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel)key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 6. 清理操作
                    if (!buffer.hasRemaining()) {
                        key.attach(null);  // 如果buffer缓冲区的内容已经写完了，则清除buffer，释放内存
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE); // 并且不再关注感兴趣的可写事件
                    }
                }
            }
        }
    }
}
