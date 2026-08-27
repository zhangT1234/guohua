package com.newgrand.controller;

import com.newgrand.domain.model.UserWechat;
import com.newgrand.service.UserWechatService;
import com.newgrand.utils.i8util.I8ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
