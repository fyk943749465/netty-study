package com.feng.server.handler;

import com.feng.message.ChatRequestMessage;
import com.feng.message.ChatResponseMessage;
import com.feng.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);

        if (channel != null) {
            // 发送信息给对方了
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            // 发送信息，给发送者
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不在线"));
        }
    }
}
