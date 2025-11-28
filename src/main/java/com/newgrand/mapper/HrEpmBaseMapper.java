package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.HrEpmBase;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 15:23
 * @Description: TODO
 */
@Resource
@Mapper
public interface HrEpmBaseMapper extends BaseMapper<HrEpmBase> {
}
