package com.feng.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class BossWorkerEventLoopServer {

    public static void main(String[] args) {

        // 这里有个问题，就是 worker 执行handler时候，某个channel 上的 handler 的执行时间较长。
        // netty 底层用到了 nio 的 selector，这是肯定会阻塞当前 worker 上其他channel 的操作。
        // 可以定义一个独立的 EventLoopGroup，专门处理耗时较长的操作
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                // boss 和 worker
                // boss 只负责 ServerSocketChannel 上的 accept 事件，worker 只负责 socketChannel 上的读写事件
                // 创建 worker EventLoopGroup 可以指定 worker 数量，会根据cpu核心数创建
                // 而boss EventLoopGroup 则不需要指定，即使指定也是只有一个线程处理accept连接事件，因为只有一个 ServerSocketChannel
                // new NioEventLoopGroup(2) 这个组里有两个 EventLoop，可以认为是有两个worker 在负责客户的读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        // 这个 ChannelInboundHandlerAdapter handler 在执行的时候，现在已经不是用 workEventLoopGroup 里的 worker 来执行了
                        // 而是交给我们定义的 group 来执行了
                        ch.pipeline().addLast( "handler1", new ChannelInboundHandlerAdapter(){
                            @Override  // 处理读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug(Thread.currentThread().getName());
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg);  // 将消息传递给下个 handler 处理
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter(){ // 把一个 handler 的执行权，交给另外一个 EventLoopGroup
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug(Thread.currentThread().getName());
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        })
                        ;
                    }
                })
                .bind(8080);
    }
}
