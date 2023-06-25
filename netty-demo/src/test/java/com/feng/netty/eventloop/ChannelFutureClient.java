package com.feng.netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 关于 ChannelFuture对象 和 connect 方法理解
 */
@Slf4j
public class ChannelFutureClient {

    public static void main(String[] args) throws InterruptedException {

        //2. 带有 Future 的类型，都是跟异步方法配套使用的，用来正确处理结果
        ChannelFuture channelFuture = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                    }
                    // connect 方法是异步非阻塞的，调用完成，立即返回，不关心结果
                    // 真正执行底层连接操作是底层的一个线程，并非主线程
                }).connect(new InetSocketAddress("localhost", 8080));
//        // 2.1 使用 sync 方法来同步处理结果
//        channelFuture.sync(); // 这里阻塞主线程，是为了等待connect异步执行之后的结果，必须要调用 sync()方法，等待
//        Channel channel = channelFuture.channel();
//        channel.writeAndFlush("hello, world");

        // 2.2 使用 addListener 方法来异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // 在 nio 线程那边，连接建立完成后，执行 operationComplete 方法
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello, world");
            }
        });
    }
}
