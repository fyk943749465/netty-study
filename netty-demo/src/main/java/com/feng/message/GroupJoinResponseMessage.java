package com.feng.message;

import lombok.Data;
import lombok.ToString;

/**
 * 加入群聊的应答
 */
@Data
@ToString(callSuper = true)
public class GroupJoinResponseMessage extends AbstractResponseMessage {

    public GroupJoinResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupJoinResponseMessage;
    }
}
