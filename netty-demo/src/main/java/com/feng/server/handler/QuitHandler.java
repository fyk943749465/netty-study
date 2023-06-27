package com.feng.server.handler;

import com.feng.server.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理客户端退出的handler，
 * 不论正常退出，还是异常退出，都要处理
 * 此 handler 不关心消息，只关心退出的正常和异常事件
 */
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接正常断开时，触发该事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开", ctx.channel());
    }

    /**
     * 当客户端发送异常时触发的事件
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 异常信息 {}", ctx.channel(), cause.getMessage());
    }
}
