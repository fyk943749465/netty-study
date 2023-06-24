package com.feng.nio.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.feng.netty.c1.utils.ByteBufferUtil.debugAll;

/**
 * 多 worker 版本
 */
public class MultiThreadServer2 {

    public static void main(String[] args) throws IOException {

        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector boss = Selector.open();
        SelectionKey sscKey = ssc.register(boss, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT); // boss selector 只处理 accept 事件

        // Runtime.getRuntime().availableProcessors() 如果工作再docker容器下，因为容器下不是物理隔离的，
        // 会拿到物理cpu个数，而不是容器申请时的个数
        // 这个问题知道 jdk 10 才修复，使用 jvm 参数 UseContainerSupport 配置，默认开启
        // 1. 创建固定数量的 worker
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i =0; i < workers.length; ++i) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
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
                    workers[index.getAndIncrement()%workers.length].register(sc);
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

