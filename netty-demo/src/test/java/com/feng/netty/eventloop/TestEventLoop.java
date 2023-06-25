package com.feng.netty.eventloop;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(); // io 事件，普通任务，定时任务 都可以处理
        //
//        EventLoopGroup group1 = new DefaultEventLoop(); // 普通任务， 定时任务都可以处理

        System.out.println( NettyRuntime.availableProcessors());
        // group.next() 代表事件循环，一个事件循环，可以负责多个channel（客户端到服务器的多个连接）
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 3. 执行普通任务，意义在于 可以执行分发的任务，将任务的执行权由一个线程转移到另一个线程
        group.next().submit(()->{  // 把一个任务，提交给事件循环组中的某一个事件循环去执行
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("ok");
        });

        // 4. 执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("ok, ok!");
        }, 1, 1, TimeUnit.SECONDS);
        log.debug("main");
    }
}
