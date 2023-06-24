package com.feng.netty.multi.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

/**
 * 多线程版本下的 selector
 */
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {

        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector boss = Selector.open();
        SelectionKey sscKey = ssc.register(boss, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT); // boss selector 只处理 accept 事件

        // 1. 创建固定数量的 worker
        Worker worker = new Worker("worker-0");
        // worker.register();;
        // 有一个问题，就是 selector.select() 方法，会阻塞 sc.register(worker.selector, SelectionKey.OP_READ, null);
        // 要保证能够注册成功，就需要保证先注册，再阻塞。不同的线程中，代码的执行顺序是没办法保证的
        // 但是可以通过一些方法做到。比如 selector.select() 再子线程先执行了，就可以调用 selector.wakeup()方法，唤醒后，让其不再阻塞
        // 不管 selector.wakeup() 在 selector.select() 方法之前调用还是之后调用，都能保证 select()方法的不阻塞。
        // 就比如，select() 阻塞后，调用了 wakeup方法，那么select()方法不阻塞。又比如，select()执行时，发现之前调用过 wakeup方法，也不会再阻塞
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {  // boss selector 感兴趣的 Accept 事件发生了，说明有客户端连接过来了
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    // worker对象 上的 selector对象 关注 sc 上发生OP_READ事件，
                    // 也就是说，发生的事件，交给了 worker 处理
                    //sc.register(worker.selector, SelectionKey.OP_READ, null);
                    worker.register(sc);
                }
            }
        }

    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                thread.start();
                selector = Selector.open();
                start = true;
            }
            selector.wakeup(); // 为了保证下面代码执行不被 selector.select()方法阻塞
            sc.register(selector, SelectionKey. OP_READ, null);
        }
        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) { // 可读
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel)key.channel();
                            // 这里有很多细节是需要处理的，比如黏包、半包问题，客户端正常断开或者异常断开等
                            // 如果还有可写事件，还要关注可写事件，并且关注是否可以一次性写完成等问题
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
