package com.feng.message;

import lombok.Data;
import lombok.ToString;

/**
 * 创建群聊的应答
 */
@Data
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupCreateResponseMessage;
    }
}
