package com.feng.protocol;

import com.feng.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 自定义协议
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1. 4 字节的魔数（魔数可以自定义）
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1 字节的版本号
        out.writeByte(1);
        //3. 1 字节的序列化方式 jdk 0, json 1
        out.writeByte(0);
        //4. 1 字节的指令类型(消息类型）
        out.writeByte(msg.getMessageType());
        //5. 4 字节 请求序号
        out.writeInt(msg.getSequenceId());
        // 写一个无意义的字节，保持长度是 2^n
        out.writeByte(0xff);
        //6. 获取内容的字节数组（java 对象转字节数组）因为，前面的序列化方式选择了0
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();

        //7. 4 字节 消息长度
        out.writeInt(bytes.length);
        //8. 写入内容
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 魔数
        int magicNum = in.readInt();
        // 版本
        byte version = in.readByte();
        // 序列化类型
        byte serializerType = in.readByte();
        // 消息类型
        byte messageType = in.readByte();
        // 消息id
        int sequenceId = in.readInt();
        // 跳过一个无异于的字节
        in.readByte();
        // 长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        // 读取内容
        in.readBytes(bytes, 0, length);

        // jdk 的序列化
        if (serializerType == 0) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message)ois.readObject();
            log.debug("{}", message);
            out.add(message);
        }
    }
}
