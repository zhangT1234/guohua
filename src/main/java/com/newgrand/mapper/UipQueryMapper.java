package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.dbo.UipQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/11/16 20:11
 */
@Repository
@Mapper
public interface UipQueryMapper extends BaseMapper<UipQuery> {
}
