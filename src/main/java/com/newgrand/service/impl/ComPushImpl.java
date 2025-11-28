package com.newgrand.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.newgrand.domain.dbo.UipQuery;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.mapper.ComSqlMapper;
import com.newgrand.mapper.UipQueryMapper;
import com.newgrand.service.ComPushService;
import com.newgrand.utils.ComHelper;
import com.newgrand.utils.StringUtils;
import com.newgrand.utils.filter.JsonValueFilter;
import com.newgrand.utils.i8util.I8ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/10/8 15:44
 */
@Service
public class ComPushImpl extends ComHelper implements ComPushService {

    @Resource
    private UipQueryMapper uipQueryMapper;
    @Autowired
    private ComSqlMapper comSqlMapper;

    @Override
    public I8ReturnModel push(String sign, String phid) {
        if (StringUtils.isEmpty(phid)) {
            return I8ResultUtil.error("单据phid字段不能为空或者null");
        }
        List<UipQuery> mainList = uipQueryMapper.selectList(Wrappers.lambdaQuery(UipQuery.class).eq(UipQuery::getBill_no, sign));
        if (mainList.size() == 0) {
            return I8ResultUtil.error("sign:{" + sign + "}对应配置不存在");
        }
        //取得主体配置
        UipQuery main = mainList.get(0);
        //取得主体查询语句
        String sql = main.getSqlstr();
        //取得主体查询主表表名
        String mainTable = main.getTablename();
        //取得主体别名
        String asName = main.getAsname();
        //回填标识
        //String syncSgin = main.getSyncsign();
        if (StringUtils.isEmpty(sql)) {
            return I8ResultUtil.error();
        }
        List<Map<String, Object>> mainDataList = GetList(phid, sql);
        if (mainDataList.size() == 0) {
            return I8ResultUtil.error("未查到对应主表信息");
        }
        //取得表体配置
        List<UipQuery> detailList = uipQueryMapper.selectList(Wrappers.lambdaQuery(UipQuery.class).eq(UipQuery::getPphid, phid));
        //取得主体json化数据
        JSONObject mainData = JSON.parseObject(JSON.toJSONString(mainDataList.get(0), new JsonValueFilter()));
        if (detailList.size() > 0) {
            for (UipQuery item : detailList) {
                //取得表体查询语句
                String dsql = item.getSqlstr();
                //取得表体别名
                String asDName = item.getAsname();
                List<Map<String, Object>> detailDataList = GetList(phid, dsql);
                mainData.put(asDName, detailDataList);
            }
        }
        if (!StringUtils.isEmpty(asName)) {
            JSONObject data = new JSONObject();
            data.put(asName, mainData);
            return I8ResultUtil.success("推送成功", JSON.toJSONString(data));
        }
        return I8ResultUtil.success("推送成功", JSON.toJSONString(mainData));
    }
}
