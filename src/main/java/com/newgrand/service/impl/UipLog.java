package com.newgrand.service.impl;

import com.newgrand.domain.model.PFormSeconddevlogM;
import com.newgrand.mapper.PFormSecondDevLogMapper;
import com.newgrand.utils.uuid.GetNewPhidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: zhanglixin
 * @Data: 2022/8/26 15:23
 * @Description: TODO
 */
@Async
@Service
public class UipLog {

    @Autowired
    private PFormSecondDevLogMapper logMapper;

    /*获得当前时间*/
    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = dtf.format(LocalDateTime.now());
        return dateString;
    }

    @Async
    public void info(String u_tabname, String u_jkms, String message) {
        try {
            InsertLog("info", u_tabname, u_jkms, message);
        } catch (Exception ex) {
            System.out.println("日志异常:" + ex.getMessage());
        }
    }

    @Async
    public void debug(String u_tabname, String u_jkms, String message) {
        try {
            InsertLog("debug", u_tabname, u_jkms, message);
        } catch (Exception ex) {

        }
    }

    @Async
    public void warn(String u_tabname, String u_jkms, String message) {
        try {
            InsertLog("warn", u_tabname, u_jkms, message);
        } catch (Exception ex) {
        }
    }

    @Async
    public void error(String u_tabname, String u_jkms, String message) {
        try {
            InsertLog("error", u_tabname, u_jkms, message);
        } catch (Exception ex) {
            System.out.println("日志异常:" + ex.getMessage());
        }
    }

    private void InsertLog(String u_level, String u_tabname, String u_jkms, String message) throws InterruptedException {
        String phid = GetNewPhidUtils.getPhid();
        Date nowTime = new Date();
        PFormSeconddevlogM build = PFormSeconddevlogM.builder()
                .phid(phid).bill_no(phid).is_wf("0").asr_flg("0").bill_dt(nowTime).fillpsn("0")
                .ischeck("1").checkpsn("0").check_dt(nowTime).pc("0").ocode("0").title(phid)
                .code("0").creator("0").editor("0").ng_insert_dt(nowTime).ng_update_dt(nowTime).ng_record_ver("1")
                .cur_orgid("0").ng_phid_org("0").ng_phid_cu("0").ng_phid_bp("0").ng_phid_original("0").ng_orgid_original("0")
                .ng_phid_ui_scheme("0").ng_sv_search_key("0").ng_sd_search_key("0").ng_share_sign("0").phid_schemeid("0")
                .ng_orgid_original("0").imp_info("").phid_cycle("0")
                .printcount("0")
                .u_level(u_level)
                .u_tabname(u_tabname)
                .u_jkms(u_jkms)
                .u_log1(message).build();

        logMapper.insert(build);
    }
}
