package com.feng.netty.single;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {

    public static void main(String[] args) {

        // 1. 服务器端的启动器 ServerBootstrap， 负责组装 netty 组件，协调 netty 的组件
        new ServerBootstrap()
                // 2. BossEventLoop, WorkerEventLoop(selector, thread)，group 组
                .group(new NioEventLoopGroup()) // 这里有事件注册 如 accept， read 事件等
                // 3. 选择一种 channel 的实现方式
                .channel(NioServerSocketChannel.class)
                // 4. boss 负责处理连接，worker 负责处理读写的，决定了 worker 能执行哪些操作（handler）
                .childHandler(

                    // 5. channel 代表和客户端进行数据读写的通道 Initializer 初始化，负责添加别的 handler
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override  // 连接建立后调用初始化方法 initChannel
                        protected void initChannel(NioSocketChannel nch) throws Exception {
                            // 6. 添加具体的 handler
                            nch.pipeline().addLast(new StringDecoder());// 解码器，因为数据传输过来都是ByteBuf，这里将ByteBuf 解码成字符串
                            nch.pipeline().addLast(new ChannelInboundHandlerAdapter(){ //自定义的处理器
                                @Override  // 读事件
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    // 打印上一个解码器转换好的字符串
                                    System.out.println(msg);
                                }
                            });
                        }
                    }
                )
                // 7. 监听端口
                .bind(8080);
    }
}
