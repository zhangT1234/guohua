package com.newgrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.newgrand.domain.dbo.UipLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：14:00 2022/11/10
 * @修改时间:：14:00 2022/11/10
 */
@Repository
@Mapper
public interface UipLogMapper extends BaseMapper<UipLog> {
}
