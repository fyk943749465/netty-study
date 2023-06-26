package com.feng.netty.pack;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class LineBasedClient {

    public static void main(String[] args) {
        send();
        System.out.println("finish......");
    }

    public static StringBuilder makeString(char c, int len) {
        StringBuilder sb = new StringBuilder(len + 1); // +2 是为了放 \n
        for (int i = 0; i < len; ++i) {
            sb.append(c);
        }
        sb.append('\n');
        return sb;
    }

    public static void send() {
        NioEventLoopGroup woker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(woker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer();
                            char c = '0';
                            Random r = new Random();
                            for (int i = 0; i< 10; ++i) {
                                StringBuilder sb = makeString(c, r.nextInt(256) + 1);
                                c ++;
                                buf.writeBytes(sb.toString().getBytes());
                            }
                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync(); // 等到连接建立后继续往下执行
            channelFuture.channel().closeFuture().sync(); // 等待关闭之后继续往下执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            woker.shutdownGracefully();
        }
    }
}
