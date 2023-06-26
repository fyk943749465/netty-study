package com.feng.netty.pack;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 粘包 半包问题
 *
 * 解决粘包半包问题，客户端发送完消息后，马上断开，这种方式可以解决粘包问题，但是没办法解决半包问题。
 *
 * 粘包问题，是由于接收端缓冲区过大，一次可以接收多条消息。造成的
 * 半包问题，是由于接收端缓冲区不足，将消息拆分开了，没能接收到完整消息。
 */
@Slf4j
public class HelloWorldClient {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i ++) {
            send();
        }
        log.info("发送完成....");
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // 会再连接 channel 建立成功之后，触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = ctx.alloc().buffer(16);
                            buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18});
                            ctx.writeAndFlush(buf); // 发送完成后
                            // 关闭 channel，是为了解决 粘包 问题，单位没办法解决半包问题，因为服务端的接收缓冲区可能不足，虽然客户端发送了一个完整消息
                            // 但是服务端，将消息拆分开了。
                            ctx.channel().close();
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
