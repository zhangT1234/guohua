package com.newgrand.utils;


import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 通用帮助
 *
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/6/22 13:39
 */
public class ComHelper {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 根据phid取数
     *
     * @param phid
     * @param sql
     * @return
     */
    public List<Map<String, Object>> GetList(String phid, String sql) {
        if (StringUtils.isEmpty(phid))
            return jdbcTemplate.queryForList(sql);
        return jdbcTemplate.queryForList(sql, phid);
    }
}
