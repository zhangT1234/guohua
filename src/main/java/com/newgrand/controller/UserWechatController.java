package com.newgrand.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.newgrand.domain.model.UserWechat;
import com.newgrand.domain.model.WechatLoginResult;
import com.newgrand.service.UserWechatService;
import com.newgrand.utils.i8util.I8ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户微信 openid 绑定
 */
@Slf4j
@Api(tags = "用户微信绑定")
@RestController
@RequestMapping("/UserWechat")
public class UserWechatController {

    @Autowired
    private UserWechatService userWechatService;

    @ApiOperation(value = "根据微信授权code获取绑定记录", notes = "用微信授权code换取openid，再查询user_wechat绑定记录；未绑定时仍返回openid")
    @RequestMapping(value = "/getByCode", method = RequestMethod.GET)
    public Object getByCode(
            @ApiParam(value = "微信授权code", required = true) @RequestParam("code") String code) {
        UserWechat userWechat = userWechatService.getByCode(code);
        if (userWechat == null) {
            return I8ResultUtil.error("获取openid失败");
        }
        if (userWechat.getUsername() == null) {
            return I8ResultUtil.success("未查询到绑定记录", userWechat);
        }
        return I8ResultUtil.success("查询成功", userWechat);
    }

    @ApiOperation(value = "根据openid和username保存绑定记录", notes = "body传入openid与username保存；若本地已存在相同openid或username则先删除再插入")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Object save(@RequestBody UserWechat request) {
        UserWechat userWechat = userWechatService.saveByOpenid(request.getOpenid(), request.getUsername());
        if (userWechat == null) {
            return I8ResultUtil.error("保存失败");
        }
        return I8ResultUtil.success("保存成功", userWechat);
    }

    @ApiOperation(value = "获取微信临时二维码", notes = "QR_STR_SCENE，scene_str为32位随机串并本地缓存600秒，返回qrUrl与sceneStr")
    @RequestMapping(value = "/getQrCode", method = RequestMethod.GET)
    public Object getQrCode() {
        Map<String, String> data = userWechatService.createTempQrCode();
        if (data == null) {
            return I8ResultUtil.error("获取二维码失败");
        }
        return I8ResultUtil.success("获取成功", data);
    }

    @ApiOperation(value = "微信扫码登录", notes = "根据sceneStr取扫码openid与绑定用户，调用/rest/api/kernelsession登录并回写Cookie")
    @RequestMapping(value = "/loginBySceneStr", method = RequestMethod.GET)
    public Object loginBySceneStr(
            @ApiParam(value = "二维码场景值", required = true) @RequestParam("sceneStr") String sceneStr,
            HttpServletResponse response) {
        WechatLoginResult result = userWechatService.loginBySceneStr(sceneStr);
        if (result == null || result.getLoginData() == null) {
            return I8ResultUtil.error("登录失败，请重新扫码");
        }
        if (!result.isSuccess()) {
            String errmsg = result.getLoginData().getString("errmsg");
            if (StringUtils.isEmpty(errmsg)) {
                errmsg = result.getLoginData().getString("Msg");
            }
            return I8ResultUtil.error(StringUtils.isEmpty(errmsg) ? "登录失败" : errmsg, result.getLoginData());
        }
        if (result.getCookies() != null) {
            for (WechatLoginResult.CookieItem item : result.getCookies()) {
                // 使用 Header 回写，避免 Cookie 值中含 $ 等字符被容器拒绝
                StringBuilder setCookie = new StringBuilder();
                setCookie.append(item.getName()).append("=").append(item.getValue());
                setCookie.append("; Path=/");
                if (item.getMaxAge() != null) {
                    setCookie.append("; Max-Age=").append(item.getMaxAge());
                }
                response.addHeader("Set-Cookie", setCookie.toString());
            }
        }
        return I8ResultUtil.success("登录成功", result.getLoginData());
    }

    @ApiOperation(value = "测试写入sceneStr缓存", notes = "手动写入二维码场景缓存与扫码openid缓存，便于联调loginBySceneStr")
    @RequestMapping(value = "/mockSceneCache", method = RequestMethod.GET)
    public Object mockSceneCache(
            @ApiParam(value = "场景值", required = true) @RequestParam("sceneStr") String sceneStr,
            @ApiParam(value = "openid", required = true) @RequestParam("openid") String openid) {
        if (!userWechatService.mockSceneCache(sceneStr, openid)) {
            return I8ResultUtil.error("写入失败，sceneStr与openid不能为空");
        }
        Map<String, String> data = new HashMap<>();
        data.put("sceneStr", sceneStr);
        data.put("openid", openid);
        data.put("sceneExpireSeconds", "600");
        data.put("scanExpireSeconds", "60");
        return I8ResultUtil.success("写入成功", data);
    }

    @ApiOperation(value = "微信服务号消息与事件推送", notes = "GET校验URL；POST接收事件（扫描带参数二维码）")
    @RequestMapping(value = "/callback", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String verifyCallback(
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {
        if (userWechatService.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        log.warn("微信URL校验失败, signature={}, timestamp={}, nonce={}", signature, timestamp, nonce);
        return "error";
    }

    @ApiOperation(value = "微信服务号事件推送接收", notes = "监听SCAN/带参关注事件，场景值命中本地缓存则缓存openid 60秒")
    @RequestMapping(value = "/callback", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String receiveCallback(
            @RequestParam(value = "signature", required = false) String signature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestBody String body) {
        if (StringUtils.isNotEmpty(signature)
                && !userWechatService.checkSignature(signature, timestamp, nonce)) {
            log.warn("微信事件推送签名校验失败");
            return "error";
        }
        try {
            userWechatService.handleEventPush(body);
        } catch (Exception e) {
            log.error("处理微信事件推送异常", e);
        }
        return "success";
    }
}
