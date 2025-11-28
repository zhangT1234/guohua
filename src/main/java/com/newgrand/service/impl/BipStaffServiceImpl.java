package com.newgrand.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.domain.dto.*;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.po.HrEpmMain;
import com.newgrand.mapper.HrEpmMainMapper;
import com.newgrand.service.BipStaffService;
import com.newgrand.service.mp.HrEpmMainService;
import com.newgrand.utils.BipRequestUtil;
import com.newgrand.utils.i8util.StringHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BipStaffServiceImpl implements BipStaffService {
    private final BipRequestUtil bipRequestUtil;
    private final HrEpmMainService hrEpmMainService;
    private final JdbcTemplate jdbcTemplate;
    private final HrEpmMainMapper hrEpmMainMapper;

    @Override
    public BipResult syncStaff(String cNo) {
        try {
            List<Map<String, Object>> epms = jdbcTemplate.queryForList(
                    " select heb.birthday,hem.*,b.bankno,hel.mobile1 from hr_epm_main hem "
                            +"  left join hr_epm_base heb on hem.phid = heb.pphid "
                            +"  left join hr_epm_finance hef on hem.phid = hef.pphid  "
                            +"  left join fg_bank b on b.phid = hem.user_ygskyh  "
                            +"  left join hr_epm_link hel on hel.pphid = hem.phid "
                            +" where hem.cno = '" + cNo + "' "
            );
            if(!epms.isEmpty()) {
                Map<String, Object> epm = epms.get(0);
                BipStaffDTO data = new BipStaffDTO();
                data.setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
                data.setId(StringHelper.nullToEmpty(epm.get("phid")));
                data.setCode(cNo);
                data.setName(StringHelper.nullToEmpty(epm.get("cname")));
                data.setCert_no(StringHelper.nullToEmpty(epm.get("cardno")));
                data.setSex(StringHelper.nullToEmpty(epm.get("sexno")));
                data.setBirthdate(StringHelper.nullToEmpty(epm.get("birthday")));
                data.setMobile(StringHelper.nullToEmpty(epm.get("mobile1")));
                BipBankAcctDTO bankAcctDTO = new BipBankAcctDTO();
                bankAcctDTO.setLinked_bank_id(StringHelper.nullToEmpty(epm.get("bankno")));
                bankAcctDTO.setAccount(StringHelper.nullToEmpty(epm.get("user_ygskzh")));
                bankAcctDTO.setId(StringHelper.nullToEmpty(epm.get("phid")));
                List<BipBankAcctDTO> bankAcctDTOs = new ArrayList<>();
                bankAcctDTOs.add(bankAcctDTO);
                data.setBankAcctList(bankAcctDTOs);
                List<BipJobDTO> mainJobList = new ArrayList<>();
                List<BipJobDTO> ptJobList = new ArrayList<>();
                List<Map<String, Object>> jobs = jdbcTemplate.queryForList(
                        " select fo.user_yyzzid org_id,fo1.user_yyzzid dept_id,hes.* from hr_epm_station hes "
                                +"  left join fg_orglist fo on hes.cboo = fo.phid "
                                +"  left join fg_orglist fo1 on hes.dept = fo1.phid "
                                +"  where pphid = " + StringHelper.nullToEmpty(epm.get("phid"))
                );
                if(!jobs.isEmpty()) {
                    for(Map<String, Object> tmpJob: jobs) {
                        BipJobDTO job = new BipJobDTO();
                        job.setId(StringHelper.nullToEmpty(tmpJob.get("phid")));
                        job.setDept_id(StringHelper.nullToEmpty(tmpJob.get("dept_id")));
                        job.setOrg_id(StringHelper.nullToEmpty(tmpJob.get("org_id")));
                        job.setBegindate(StringHelper.nullToEmpty(tmpJob.get("bdt")));
                        if("0".equals(StringHelper.nullToEmpty(tmpJob.get("assigntype")))) {
                            mainJobList.add(job);
                        } else {
                            ptJobList.add(job);
                        }
                    }
                }
                data.setMainJobList(mainJobList);
                data.setPtJobList(ptJobList);

                BipRequest<BipStaffDTO> param = new BipRequest<>();
                param.setData(data);
                System.out.println("data:" + JSONObject.toJSONString(param));
                BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/i8/staffs/save/convert", JSONObject.toJSONString(param));
                System.out.println(resultData.toString());
                if("200".equals(resultData.getCode())) {
                    UpdateWrapper updateWrapper = new UpdateWrapper();
                    updateWrapper.eq("phid", epm.get("phid"));
                    updateWrapper.set("user_bip_no", ((JSONObject)resultData.getData()).getString("id"));
                    hrEpmMainMapper.update(null, updateWrapper);
                }
                return resultData;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult testSyncStaff(BipRequest<BipStaffDTO> data) {
        try {

            data.getData().setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
            BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/i8/staffs/save/convert", JSONObject.toJSONString(data));
            log.info(resultData.toString());
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
