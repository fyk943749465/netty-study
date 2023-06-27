package com.feng.protocol;

import com.feng.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * LoggingHandler 是一个无状态的 handler，即收到多少数据，打印多少数据
 * 而 LengthFieldBasedFrameDecoder 是一个有状态的 handler，
 * 因为它需要收到一个完整的报文进行处理，而一个完整报文并不一定是一次性收到的
 *
 * 无状态的handler 就是线程安全的
 * 而有状态的 handler 是线程不安全的
 *
 * @Sharable 注解的 handler，是线程安全的 handler，是可以多线程环境下使用的
 * 而没有加 @Sharable 注解的 handler 则不能在多线程环境下使用
 */
public class TestEmbeddedCodec {

    public static void main(String[] args) throws Exception {

        // LengthFieldBasedFrameDecoder 解码器收到的报文，如果发现是个被切断的报文，就会继续等待收取，不会到MessageCodec
        // 只有收到完整的消息报文，才会将消息继续向下传播
        EmbeddedChannel channel = new EmbeddedChannel(
                // 解决粘包半包问题
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec());

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        // 对于客户端来说，出站消息是需要编码的
        channel.writeOutbound(message);

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);

        ByteBuf s1 = buf.slice(0, 100);
        ByteBuf s2 = buf.slice(100, buf.readableBytes()-100);
        //证明半包问题发生后， 调用与不调用LengthFieldBasedFrameDecoder 解码器的效果

        s1.retain(); // 引用计数+1，是为了后面写s2时不报错
        channel.writeInbound(s1); // 写完之后，s1对应的内存就会被 release 掉，此时在调用s2会报错，因为s1和s2本质上都是buf的内存
        channel.writeInbound(s2); // 这里调用会报错，为了避免报错，需要维持引用计数不为0
    }
}
