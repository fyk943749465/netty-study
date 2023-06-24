package com.feng.netty.single;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {


    public static void main(String[] args) throws InterruptedException {
        // 1. 启动客户端的启动器类
        new Bootstrap()
                // 2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择客户端 channel 实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 在连接建立后调用初始化方法 initChannel
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()); // 编码
                    }
                })
                // 5. 连接到服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync() //直到连接建立，才会执行该方法
                .channel() // 服务器和客户端的连接对象
                // 6. 向服务器发送数据
                .writeAndFlush("Hello, world") // 发送数据，就会执行处理器的内部方法，即 StringEncoder 里的方法
                ;
    }
}
