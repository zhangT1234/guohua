package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.HrEpmLink;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 15:24
 * @Description: TODO
 */
@Resource
@Mapper
public interface HrEpmLinkMapper extends BaseMapper<HrEpmLink> {
}
