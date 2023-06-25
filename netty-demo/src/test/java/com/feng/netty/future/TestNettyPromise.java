package com.feng.netty.future;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * 开发网络编程的 rpc 框架，就非常有用了
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        DefaultPromise<Integer> promise = new DefaultPromise<>(group.next());

        new Thread(()->{
            log.debug("开始计算");
            try {
                int i = 1/0;
                Thread.sleep(1000);
                promise.setSuccess(80);
            } catch (Exception e) {
                promise.setFailure(e);
            }

        }).start();

        // 接收线程结果
        log.debug("等待结果");
        // get 方法是阻塞方法，等待线程执行结果的返回
        log.debug("结果是{}", promise.get());
    }
}
