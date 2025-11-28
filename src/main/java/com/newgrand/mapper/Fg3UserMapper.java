package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.Fg3User;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/8/26 18:09
 * @Description: TODO
 */
@Resource
@Mapper
public interface Fg3UserMapper extends BaseMapper<Fg3User> {

}
