package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.po.HrEpmMain;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

@Resource
@Mapper
public interface HrEpmMainMapper extends BaseMapper<HrEpmMain> {
}
