package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.dto.AccRequest;
import com.newgrand.domain.model.Fc3OuterAcc;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.service.AccountService;
import com.newgrand.service.Fc3OuterAccService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private Fc3OuterAccService fc3OuterAccService;
    @Autowired
    private I8Request i8Request;
    @Autowired
    private FgOrglistService fgOrglistService;

    private static final String mstformDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"AccCode\":\"20260112-0001\",\"AccType\":1,\"AccName\":\"中国人民银行营业管理部营业室\",\"PhidOpenOrg\":\"114190218000001\",\"AccountName\":\"杭州新中大科技股份有限公司\",\"PhidBank\":\"2\",\"AccountNo\":\"1112 3365 4785 5\",\"BsName\":\"中国人民银行\",\"BankNo\":\"0011 0000 1509 \",\"IsEbank\":0,\"IncomType\":3,\"FinAcUnionKey\":\"\",\"IsShare\":0,\"PhidShareOrg\":\"\",\"PhidSourceAcc\":\"\",\"PhidOrg\":\"114190218000001\",\"Remark\":\"\",\"IsStop\":0,\"IsDefault\":0,\"BankSalaryProjNo\":\"\",\"BankSalaryProtocol\":\"\",\"IsGroup\":0,\"NBEbankCode\":\"\",\"BankLhh\":\"\",\"PhidEbankInfo\":\"\",\"FinAcCode\":\"\",\"PhId\":\"\",\"NgInsertDt\":\"\",\"NgUpdateDt\":\"\",\"NgRecordVer\":\"\",\"CurOrgId\":\"\",\"Creator\":\"\",\"Editor\":\"\",\"key\":\"\"}}}";

    @Override
    public I8ReturnModel save(AccRequest data){
        log.info("收款信息同步入参: {}", JSONObject.toJSONString(data));
            try {
                LambdaQueryWrapper<Fc3OuterAcc> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Fc3OuterAcc::getUserOfsid, data.getUserOfsid());
                List<Fc3OuterAcc> list = fc3OuterAccService.list(queryWrapper);
                if (list.isEmpty()) {
                    //新增
                    List<NameValuePair> urlParameters = new ArrayList<>();
                    HashMap<String, Object> mapInfo = new HashMap<>();

                    mapInfo.put("AccCode", data.getAccCode());
                    mapInfo.put("AccName", data.getAccName());
                    mapInfo.put("PhidBank", data.getPhidBank());
                    mapInfo.put("AccountNo", data.getAccountNo());
                    mapInfo.put("AccountName", data.getAccountName());
                    mapInfo.put("AccType", data.getAccType());
                    mapInfo.put("IncomType", data.getIncomType());
                    mapInfo.put("IsStop", data.getIsStop());
                    mapInfo.put("user_ofsid", data.getUserOfsid());

                    LambdaQueryWrapper<FgOrglist> orgWrapper = new LambdaQueryWrapper<>();
                    orgWrapper.eq(FgOrglist::getUserOfsid, data.getPhidOrg())
                            .eq(FgOrglist::getOrgtype, "Y");
                    List<FgOrglist> orgList = fgOrglistService.list(orgWrapper);
                    if (CollectionUtil.isNotEmpty(orgList)) {
                        mapInfo.put("PhidOpenOrg", orgList.get(0).getPhid());
                        mapInfo.put("PhidOrg", orgList.get(0).getPhid());
                    }

                    String mstFormData = I8Converter.SetField(mstformDataStr, mapInfo);

                    urlParameters.add(new BasicNameValuePair("mstformData", mstFormData));
                    urlParameters.add(new BasicNameValuePair("ng3_logid", "315211026000006"));

                    log.info("调用收款信息的param:{}", JSONObject.toJSONString(urlParameters));
                    I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/FC/BasicData/OuterAcc/save", urlParameters);
                    log.info(data.getAccCode() + "---收款信息同步结果: {}", JSONObject.toJSONString(i8ReturnModel));
                    if (!i8ReturnModel.getIsOk()) {
                        return i8ReturnModel;
                    }
                    return I8ResultUtil.success("收款信息" + "新增成功");
                } else {
                    return I8ResultUtil.success("收款信息" + "更新成功");
                }
            } catch (Exception e) {
                return I8ResultUtil.error(e.getMessage());
            }
        }

}
