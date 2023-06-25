package com.feng.netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 入站处理器  顺序执行
 * 出站处理器  逆序执行（只有执行了 向客户端写操作，才会触发出站操作）
 */
@Slf4j
public class TestPipeline {

    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        // 添加处理， netty 会自动增加两个 handler: header 和 tail
                        // header -> h1 -> h2 -> h3 -> tail
                        ChannelPipeline pipeline = ch.pipeline();
                        // 入站处理器
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2");
                                super.channelRead(ctx, msg);

                                // 一个是ctx 一个是 ch，都调用writeAndFlush 操作
                                // 但是两个操作行为是不一样的
                                // ch.writeAndFlush 是从 tail 开始向前找出站处理器，然后执行出站处理器
                                // ctx.writeAndFlush 是从 当前处理器，开始向前找出站处理器。
                                // 两个对象都能调用 writeAndFlush ，但是找出站处理器的起始位置是不一样的
                                // ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server".getBytes()));
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server".getBytes()));
                            }
                        });

                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3");
                                super.channelRead(ctx, msg);
                            }
                        });

                        // 出站处理器，只有本程序执行了 writeAndFlush 方法，才会触发出站处理器
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("h4");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("h5");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h6", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("h6");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                }).bind(8080);
    }
}
