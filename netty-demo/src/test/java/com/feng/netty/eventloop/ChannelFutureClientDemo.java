package com.feng.netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 客户端建立连接后，源源不断向服务器发送信息，
 * 但输入 q 时候，退出
 * 退出时候，如何做善后工作
 */
@Slf4j
public class ChannelFutureClientDemo {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap().group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = channelFuture.sync().channel();
        log.debug("{}", channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    channel.close();// close 方法是一个异步操作
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();
        // 获取ClosedFuture ，两种处理方式 1同步模式，2异步模式

        ChannelFuture closeFuture = channel.closeFuture();
        // 方式 1
//        log.debug("等待关闭........");
//        closeFuture.sync(); // 这里会阻塞，等待连接关闭后继续向下执行
//        log.debug("连接关闭后，执行下面代码");

        // 方式 2，连接关闭后，在另外一个线程中执行善后工作
        closeFuture.addListener(new ChannelFutureListener() {
            @Override // 当 channle close 之后，会回调执行这个方法，这里需要 关闭客户端，
            // 否则即使 channel 关闭，client 依然在允许，是因为有些线程还在继续执行
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("连接关闭后，执行下面代码");
                eventExecutors.shutdownGracefully(); // 优雅关闭 client
            }
        });
    }
}
