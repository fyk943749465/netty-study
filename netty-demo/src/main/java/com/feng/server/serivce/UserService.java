package com.feng.server.serivce;

/**
 * 用户管理接口
 */
public interface UserService {

    /**
     * 登录
     */
    boolean login(String username, String password);
}
