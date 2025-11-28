package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.PFormSeconddevlogM;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/8/26 15:20
 * @Description: 日志操作类
 */
@Resource
@Mapper
public interface PFormSecondDevLogMapper extends BaseMapper<PFormSeconddevlogM> {

}
