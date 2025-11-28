package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.Secuser;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2023/6/3 22:23
 * @Description: TODO
 */
@Resource
@Mapper
public interface SecuserMapper extends BaseMapper<Secuser> {
}
