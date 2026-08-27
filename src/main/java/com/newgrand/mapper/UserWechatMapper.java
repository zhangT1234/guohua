package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.UserWechat;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * 用户 openid 绑定表 Mapper
 */
@Resource
@Mapper
public interface UserWechatMapper extends BaseMapper<UserWechat> {

}
