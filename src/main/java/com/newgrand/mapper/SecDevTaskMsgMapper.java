package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.model.SecDevTaskMsg;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/20 17:50
 * @Description: TODO
 */
@Resource
@Mapper
public interface SecDevTaskMsgMapper extends BaseMapper<SecDevTaskMsg> {

}
