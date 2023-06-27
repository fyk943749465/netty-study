package com.feng.server.handler;

import com.feng.message.LoginRequestMessage;
import com.feng.message.LoginResponseMessage;
import com.feng.server.serivce.UserServiceFactory;
import com.feng.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {

        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        if (login){
            SessionFactory.getSession().bind(ctx.channel(), username);
        }
        LoginResponseMessage message = new LoginResponseMessage(login, login ? "登录成功" : "登录失败");
        ctx.writeAndFlush(message);// 写到客户端去
    }
}
