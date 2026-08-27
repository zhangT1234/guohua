package com.newgrand.service;

import com.newgrand.domain.model.UserWechat;

/**
 * 用户 openid 绑定服务
 */
public interface UserWechatService {

    /**
     * 根据用户名查询绑定信息
     */
    UserWechat getByUsername(String username);

    /**
     * 根据 openid 查询绑定信息
     */
    UserWechat getByOpenid(String openid);

    /**
     * 根据微信授权 code 换取 openid 并查询绑定记录
     */
    UserWechat getByCode(String code);

    /**
     * 根据 openid、username 保存绑定关系；
     * 若本地已存在该 openid 或 username 的记录则先删除再保存
     */
    UserWechat saveByOpenid(String openid, String username);

    /**
     * 绑定用户与 openid（已存在则更新）
     */
    boolean bind(String username, String openid);

    /**
     * 新增绑定记录
     */
    boolean save(UserWechat userWechat);

    /**
     * 更新绑定记录
     */
    boolean update(UserWechat userWechat);
}
