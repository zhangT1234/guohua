package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.dbo.UIPCommon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/28 9:57
 * @Description: TODO
 */
@Mapper
public interface UIPCommonMapper extends BaseMapper<UIPCommon> {

    @Select(" ${querysql} ")
    List<Map<String, Object>> dynamicSql(@Param("querysql") String querysql);

}
