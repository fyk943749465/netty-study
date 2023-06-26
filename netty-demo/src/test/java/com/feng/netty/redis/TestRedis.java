package com.feng.netty.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * 按照redis的协议，给redis发送数据
 * 协议解析：
 * set key value
 *  *3 要求，先发 * 后面的数字代表元素的个数
 *  接下来，发送每个命令，以及每个键值的长度
 *  $3 set
 *  $4 name
 *  $8 zhangsan
 *
 *  多个部分之间，要用回车或者换行加以分割
 */
public class TestRedis {

    public static void main(String[] args) {

        final byte[] DELIMITER = {13, 10};

        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("*3".getBytes());
                            buffer.writeBytes(DELIMITER);
                            // set
                            buffer.writeBytes("$3".getBytes());
                            buffer.writeBytes(DELIMITER);
                            buffer.writeBytes("set".getBytes());
                            buffer.writeBytes(DELIMITER);
                            // name
                            buffer.writeBytes("$4".getBytes());
                            buffer.writeBytes(DELIMITER);
                            buffer.writeBytes("name".getBytes());
                            buffer.writeBytes(DELIMITER);
                            // zhangsan
                            buffer.writeBytes("$8".getBytes());
                            buffer.writeBytes(DELIMITER);
                            buffer.writeBytes("zhangsan".getBytes());
                            buffer.writeBytes(DELIMITER);

                            ctx.writeAndFlush(buffer);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("redis-server", 6380).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
