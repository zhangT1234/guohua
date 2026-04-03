package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.dto.ReceiptNoticeRequest;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.Fg3EnterpriseService;
import com.newgrand.service.ReceiptNoticeService;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ReceiptNoticeServiceImpl implements ReceiptNoticeService {

    @Autowired
    private I8Request i8Request;
    @Autowired
    private Fg3EnterpriseService fg3EnterpriseService;

    private static final String mstformDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"BillCode\":\"\",\"BillName\":\"测试2222\",\"RecDate\":\"2026-01-22\",\"RecFlag\":3,\"RecAmtFc\":100,\"TakeAmtFc\":30,\"ChangeAmtFc\":70,\"PhidPayEnt\":\"823000000000001\",\"PayAccName\":\"\",\"PhidPayBank\":\"\",\"PayAccNo\":\"\",\"PhidRecOrg\":\"\",\"PhidRecAcc\":\"\",\"PhidRecBank\":\"\",\"RecAccNo\":\"\",\"PhidAccountOrg\":\"823000000000003\",\"PhidPayWay2\":\"\",\"FbAmt\":0,\"PhidProjMgrPsn\":\"\",\"PhidEmployee\":\"\",\"InputSource\":0,\"Remark\":\"\",\"UntakeReason\":\"\",\"RecAmt\":100,\"TakeAmt\":30,\"RestRecAmt\":100,\"RestRecAmtFc\":100,\"RestTakeAmt\":30,\"RestTakeAmtFc\":30,\"RestChangeAmt\":70,\"RestChangeAmtFc\":70,\"ChangeAmt\":70,\"PhidPayWay1\":\"\",\"BillNo1\":\"\",\"PhidBillClass1\":\"\",\"RecAccName\":\"\",\"PhidBelongOrg\":\"\",\"PhIdFcur\":\"1\",\"ExchRate\":1,\"PhidFundType\":\"\",\"PhidSchemeid\":\"376210617000003\",\"DistributionChannel\":\"\",\"PhidFb\":\"\",\"IsSendMsg\":\"0\",\"IsSendByCus\":\"\",\"RedFlag\":\"\",\"PhidScmBill\":\"\",\"BillType\":\"2\",\"PrintCount\":\"\",\"PrintDate\":\"\",\"ErMode\":1,\"PhId\":\"\",\"NgRecordVer\":\"\",\"TrNum\":\"\",\"TrType\":\"\",\"TrAcc\":\"\",\"TrDate\":\"\",\"Finflag\":\"\",\"WfFlag\":\"\",\"AttFlag\":\"\",\"DocFlag\":\"\",\"RefFbFlag\":\"\",\"ImpInfo\":\"\",\"PhidSourcemid\":\"\",\"ItemResource\":\"\",\"ResourceType\":\"\",\"GridData\":\"\",\"BillNo2\":\"\",\"PhidBillClass2\":\"\",\"PhidBillType\":\"\",\"LssueDate\":\"\",\"ExpireDate\":\"\",\"BillAmt\":\"\",\"LssueEntName\":\"\",\"PhidLssueBank\":\"\",\"LssueAccNo\":\"\",\"FbAcceptorName\":\"\",\"PhidAcceptorBank\":\"\",\"FbAcceptorAccNo\":\"\",\"TransferFlag\":\"\",\"SignFlag\":0,\"StateDescription\":\"\",\"IsBlackAcceptor\":0,\"IsBackFb\":0,\"EBankBillType\":\"\",\"FbRiskManagement\":\"\",\"IsSplit\":0,\"PhidLssueEnt\":\"\",\"BankIfFlag\":\"\",\"IsSendByCusShow\":\"0\",\"IsSendMsgOn\":\"0\",\"PhidInputPsn\":\"614210413000001\",\"InputDate\":\"2026-01-22\",\"PhidCheckPsn\":\"\",\"CheckDate\":\"\",\"CheckFlag\":\"\",\"TakeStatus\":0,\"PhidOrg\":\"823000000000003\",\"key\":\"\"}}}";

    @Override
    public I8ReturnModel saveReceiptNotice(ReceiptNoticeRequest data) {
        log.info("到账通知单同步入参: {}", JSONObject.toJSONString(data));
        try {
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();
                if (data.getRecAmtFc() == null) {
                    data.setRecAmtFc(new BigDecimal(0));
                }
                if (data.getTakeAmtFc() == null) {
                    data.setTakeAmtFc(new BigDecimal(0));
                }
                mapInfo.put("BillName", data.getBillName());
                mapInfo.put("RecFlag", data.getRecFlag());
                mapInfo.put("RecAmtFc", data.getRecAmtFc());
                mapInfo.put("RecAmt", data.getRecAmtFc());
                mapInfo.put("RestRecAmt", data.getRecAmtFc());
                mapInfo.put("RestRecAmtFc", data.getRecAmtFc());
                mapInfo.put("TakeAmtFc", data.getTakeAmtFc());
                mapInfo.put("TakeAmt", data.getTakeAmtFc());
                mapInfo.put("RestTakeAmt", data.getTakeAmtFc());
                mapInfo.put("RestTakeAmtFc", data.getTakeAmtFc());
                mapInfo.put("ChangeAmtFc", data.getRecAmtFc().subtract(data.getTakeAmtFc()));
                mapInfo.put("RestChangeAmt", data.getRecAmtFc().subtract(data.getTakeAmtFc()));
                mapInfo.put("RestChangeAmtFc", data.getRecAmtFc().subtract(data.getTakeAmtFc()));
                mapInfo.put("ChangeAmt", data.getRecAmtFc().subtract(data.getTakeAmtFc()));
                mapInfo.put("user_yh", data.getUserYh());
                LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Fg3Enterprise::getUserOfsid, data.getUserOfsid());
                List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    mapInfo.put("PhidPayEnt", list.get(0).getPhid());
                }

                String mstFormData = I8Converter.SetField(mstformDataStr, mapInfo);

                urlParameters.add(new BasicNameValuePair("receiptnoticeformData", mstFormData));
                urlParameters.add(new BasicNameValuePair("pointid", ""));
                urlParameters.add(new BasicNameValuePair("type", "0"));
                urlParameters.add(new BasicNameValuePair("ng3_logid", "315211026000006"));
                log.info("到账通知单调用的param:{}", JSONObject.toJSONString(urlParameters));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/FC/EFM/ReceiptNotice/save", urlParameters);
                log.info(data.getBillName() + "---到账通知单同步结果: {}", JSONObject.toJSONString(i8ReturnModel));
                if (!i8ReturnModel.getIsOk()) {
                    return i8ReturnModel;
                }
                return I8ResultUtil.success("到账通知单" + data.getBillName() + "新增成功");
            } catch (Exception e) {
               return I8ResultUtil.error(e.getMessage());
            }
    }

}
