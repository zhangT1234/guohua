package com.newgrand.utils.i8util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPhIdHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据参数查询phid
     *
     * @param tname     表名
     * @param codeName  字段名
     * @param codeValue 值
     * @return
     */
    public String GetPhIdByCode(String tname, String codeName, String codeValue) {
        String r = "";
        String sql = "select phid from " + tname + " where " + codeName + "='" + codeValue + "'";
        List<String> list = jdbcTemplate.queryForList(sql, String.class);
        if (list.size() > 0) {
            r = list.get(0);
        }
        return r;
    }

    /**
     * 根据主键查询指定字段值
     *
     * @param tname     表名
     * @param phid  主键值
     * @param fieldName 要查询的字段
     * @return
     */
    public String GetValueByphid(String tname, String phid, String fieldName) {
        String r = "";
        if(fieldName == null || "".equals(fieldName)) {
            return r;
        }
        String sql = "select " + fieldName + " from " + tname + " where phid='" + phid + "'";
        List<String> list = jdbcTemplate.queryForList(sql, String.class);
        if (list.size() > 0) {
            r = list.get(0);
        }
        return r;
    }

    /**
     * 根据参数查询明细表的phid
     *
     * @param tname     表名
     * @param codeName  字段名
     * @param codeValue 值
     * @param phid      主表phid
     * @return
     */
    public String GetPhIdByCodeDtl(String tname, String codeName, String codeValue, String phid) {
        String r = "";
        String sql = "select phid from " + tname + " where " + codeName + "='" + codeValue + "'" + " and pphid=" + phid;
        List<String> list = jdbcTemplate.queryForList(sql, String.class);
        if (list.size() > 0) {
            r = list.get(0);
        }
        return r;
    }
}