package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.po.FgOrglist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 10:17
 * @Description: TODO
 */
@Resource
@Mapper
public interface FgOrglistMapper extends BaseMapper<FgOrglist> {
}
