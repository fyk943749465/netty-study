package com.feng.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * 创建群聊的请求
 */
@Data
@ToString(callSuper = true)
public class GroupCreateRequestMessage extends Message {
    private String groupName;
    private Set<String> members;

    public GroupCreateRequestMessage(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupCreateRequestMessage;
    }
}
