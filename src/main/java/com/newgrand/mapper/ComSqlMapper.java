package com.newgrand.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：14:31 2022/9/15
 * @修改时间:：14:31 2022/9/15
 */
@Repository
@Mapper
public interface ComSqlMapper {

    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> queryObject(@Param("sql") String sql);

    String queryString(@Param("sql") String sql);

    Integer update(@Param("sql") String sql);

    Integer insert(@Param("sql") String sql);
}
