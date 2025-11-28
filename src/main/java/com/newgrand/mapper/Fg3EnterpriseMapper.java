package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.Fg3Enterprise;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 11:27
 * @Description: 往来单位
 */

@Resource
@Mapper
public interface Fg3EnterpriseMapper extends BaseMapper<Fg3Enterprise> {

}
