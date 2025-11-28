package com.newgrand.utils;

import com.newgrand.domain.dbo.UipLog;
import com.newgrand.mapper.UipLogMapper;
import com.newgrand.utils.uuid.NewPhidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：14:16 2022/11/10
 * @修改时间:：14:16 2022/11/10
 */
@Slf4j
public class NGLog {

    @Autowired
    private UipLogMapper uipLogMapper;

//    @Async("asyncServiceExecutor")
//    public void info(String sign, String describe) {
//        InsertLog("info", sign, describe, "");
//    }

    @Async("asyncServiceExecutor")
    public void info(String sign, String describe, String info) {
        InsertLog("info", sign, describe, info);
    }

//    @Async("asyncServiceExecutor")
//    public void error(String sign, String describe) {
//        InsertLog("error", sign, describe, "");
//    }

    @Async("asyncServiceExecutor")
    public void error(String sign, String describe, String info) {
        InsertLog("error", sign, describe, info);
    }

    @Async("asyncServiceExecutor")
    public void warn(String sign, String describe, String info) {
        InsertLog("warn", sign, describe, info);
    }

    @Async("asyncServiceExecutor")
    public void debug(String sign, String describe, String info) {
        InsertLog("debug", sign, describe, info);
    }

    private void InsertLog(String level, String sign, String describe) {
        InsertLog(level, sign, describe, "", "", "");
    }

    private void InsertLog(String level, String sign, String describe, String info) {
        InsertLog(level, sign, describe, info, "", "");
    }

    private void InsertLog(String level, String sign, String describe, String info, String relation_table, String relation_id) {
        try {
            Thread.sleep(10);
            String phid = NewPhidUtils.getPhid();
            Date date = new Date();
            java.sql.Date dateTime = new java.sql.Date(date.getTime());
            UipLog main = UipLog.builder()
                    .phid(phid)
                    .level(level)
                    .sign(sign)
                    .describe(describe)
                    .info(info)
                    .ng_insert_dt(dateTime)
                    .ng_update_dt(dateTime)
                    .relation_table(relation_table)
                    .relation_id(relation_id)
                    .build();
            uipLogMapper.insert(main);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
