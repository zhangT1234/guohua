package com.newgrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.newgrand.domain.model.UserWechat;
import com.newgrand.mapper.UserWechatMapper;
import com.newgrand.service.UserWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户 openid 绑定服务实现
 */
@Service
public class UserWechatServiceImpl implements UserWechatService {

    @Autowired
    private UserWechatMapper userWechatMapper;

    @Autowired
    private WorkFlowService workFlowService;

    @Override
    public UserWechat getByUsername(String username) {
        return userWechatMapper.selectOne(
                new QueryWrapper<UserWechat>().eq("username", username));
    }

    @Override
    public UserWechat getByOpenid(String openid) {
        return userWechatMapper.selectOne(
                new QueryWrapper<UserWechat>().eq("openid", openid));
    }

    @Override
    public UserWechat getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        String openid = workFlowService.getOpenid(code);
        if (StringUtils.isEmpty(openid)) {
            return null;
        }
        UserWechat userWechat = getByOpenid(openid);
        // 未绑定也返回 openid，便于前端继续走绑定流程
        if (userWechat == null) {
            return UserWechat.builder().openid(openid).build();
        }
        return userWechat;
    }

    @Override
    public UserWechat saveByOpenid(String openid, String username) {
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(username)) {
            return null;
        }
        // 本地已有 openid 或 username 对应关系时先删除
        userWechatMapper.delete(new QueryWrapper<UserWechat>()
                .eq("openid", openid)
                .or()
                .eq("username", username));

        Date now = new Date();
        UserWechat entity = UserWechat.builder()
                .username(username)
                .openid(openid)
                .insertDt(now)
                .updateDt(now)
                .build();
        if (userWechatMapper.insert(entity) > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean bind(String username, String openid) {
        Date now = new Date();
        UserWechat exist = getByUsername(username);
        if (exist == null) {
            UserWechat entity = UserWechat.builder()
                    .username(username)
                    .openid(openid)
                    .insertDt(now)
                    .updateDt(now)
                    .build();
            return userWechatMapper.insert(entity) > 0;
        }
        exist.setOpenid(openid);
        exist.setUpdateDt(now);
        return userWechatMapper.updateById(exist) > 0;
    }

    @Override
    public boolean save(UserWechat userWechat) {
        if (userWechat.getInsertDt() == null) {
            userWechat.setInsertDt(new Date());
        }
        if (userWechat.getUpdateDt() == null) {
            userWechat.setUpdateDt(new Date());
        }
        return userWechatMapper.insert(userWechat) > 0;
    }

    @Override
    public boolean update(UserWechat userWechat) {
        userWechat.setUpdateDt(new Date());
        return userWechatMapper.updateById(userWechat) > 0;
    }
}
