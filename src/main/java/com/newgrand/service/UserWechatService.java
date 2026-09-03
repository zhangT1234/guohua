package com.newgrand.service;

import com.newgrand.domain.model.UserWechat;
import com.newgrand.domain.model.WechatLoginResult;

import java.util.Map;

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
     * 创建临时字符串场景二维码（QR_STR_SCENE，有效期600秒），
     * 将32位 scene_str 本地缓存（不重复），返回 qrUrl 与 sceneStr
     *
     * @return key: qrUrl、sceneStr；失败返回 null
     */
    Map<String, String> createTempQrCode();

    /**
     * 校验微信服务器签名
     */
    boolean checkSignature(String signature, String timestamp, String nonce);

    /**
     * 处理微信事件推送（扫描带参数二维码等）
     */
    void handleEventPush(String xml);

    /**
     * 根据 sceneStr 从扫码缓存取 openid，再查绑定用户并调用 kernelSessionNew 登录
     *
     * @param sceneStr 二维码场景值
     * @return 登录结果及需回写的 Cookie；失败返回 null
     */
    WechatLoginResult loginBySceneStr(String sceneStr);

    /**
     * 测试用：手动写入 sceneStr 二维码缓存，以及 sceneStr→openid 扫码缓存
     *
     * @param sceneStr 场景值
     * @param openid   扫码用户 openid
     * @return 是否写入成功
     */
    boolean mockSceneCache(String sceneStr, String openid);

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
