package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.newgrand.domain.dto.CapExDetailRequest;
import com.newgrand.domain.dto.CapExRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.CapExService;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CapExServiceImpl implements CapExService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public I8ReturnModel updateCapEx(CapExRequest capExRequest){
        if  ("payBill".equals(capExRequest.getCapExType())) {
             if (capExRequest.getPayStatus() == 0 ) {
                 //失败 更新支付状态 3
                 jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ? where bill_code = ?", 3, capExRequest.getBillNo());
             } else if (capExRequest.getPayStatus() == 1 ) {
                 //成功 更新支付状态 2
                 List<Map<String, Object>> fc3PayBill = jdbcTemplate.queryForList(
                         "SELECT phid,appprove_amt_fc,phid_sourcemid FROM fc3_pay_bill where bill_code = '" + capExRequest.getBillNo() + "'"
                 );
                 String phidSourcemId = fc3PayBill.get(0).get("phid_sourcemid")!=null?fc3PayBill.get(0).get("phid_sourcemid").toString():"";
                 String appproveAmtFc = fc3PayBill.get(0).get("appprove_amt_fc")!=null?fc3PayBill.get(0).get("appprove_amt_fc").toString():"0";
                 BigDecimal appproveAmtFcDecimal  = new BigDecimal(appproveAmtFc);

                 List<Map<String, Object>> fc3PayplanPc = jdbcTemplate.queryForList(
                         "select phid,rest_amt,rest_amt_fc,plan_status from fc3_payplan_pc where phid = '" + phidSourcemId + "'"
                 );
                 if (CollectionUtil.isNotEmpty(fc3PayplanPc)) {
                      for (Map<String, Object> fc3PayplanPcMap : fc3PayplanPc) {
                            String phid = fc3PayplanPcMap.get("phid")!=null?fc3PayplanPcMap.get("phid").toString():"";
                            String restAmt = fc3PayplanPcMap.get("rest_amt")!=null?fc3PayplanPcMap.get("rest_amt").toString():"0";
                            String restAmtFc = fc3PayplanPcMap.get("rest_amt_fc")!=null?fc3PayplanPcMap.get("rest_amt_fc").toString():"0";
                            BigDecimal  restAmtDecimal  = new BigDecimal(restAmt);
                            BigDecimal  restAmtFcDecimal  = new BigDecimal(restAmtFc);
                            BigDecimal  restAmtResult = restAmtDecimal.subtract(appproveAmtFcDecimal);
                            BigDecimal  restAmtFcResult = restAmtFcDecimal.subtract(appproveAmtFcDecimal);
                            int fc3PayplanPcPayStatus = 3;
                            if (restAmtResult.compareTo(BigDecimal.ZERO) > 0) {
                                fc3PayplanPcPayStatus = 2;
                            }
                            jdbcTemplate.update("UPDATE fc3_payplan_pc set plan_status = ? , rest_amt = ? , rest_amt_fc = ? where phid = ?", fc3PayplanPcPayStatus, restAmtResult, restAmtFcResult, phid);
                      }
                 }
                 List<CapExDetailRequest> list = capExRequest.getDetail();
                 if (CollectionUtil.isNotEmpty(list)) {
                       for (CapExDetailRequest capExDetailRequest : list) {
                           //明细数据
                           List<Map<String, Object>> fc3PayBillDet = jdbcTemplate.queryForList(
                                   "select phid,phid_mst,phid_proj,ac_code,apply_amt_fc,phid_sourcemid,phid_sourceid,approve_amt_fc FROM fc3_pay_bill_det WHERE phid= '" + capExDetailRequest.getXzdid() + "'"
                           );
                           if (CollectionUtil.isNotEmpty(fc3PayBillDet)) {
                                  for (Map<String, Object> map : fc3PayBillDet) {
                                       String phidSourceid = map.get("phid_sourceid") != null ? map.get("phid_sourceid").toString() : "";
                                       String approveAmtFc = map.get("approve_amt_fc") != null ? map.get("approve_amt_fc").toString() : "0";
                                       BigDecimal approveAmtFcDecimal  = new BigDecimal(approveAmtFc);
                                       List<Map<String, Object>> fc3PayplanPcDd = jdbcTemplate.queryForList(
                                              "select phid,rest_amt,rest_amt_fc,realpay_amt,realpay_amt_fc,phid_sourceid,phid_det from fc3_payplan_pc_dd where phid='" + phidSourceid + "'"
                                       );
                                       if (CollectionUtil.isNotEmpty(fc3PayplanPcDd)) {
                                            for (Map<String, Object> fc3PayplanPcDdMap : fc3PayplanPcDd) {
                                                  String rest_amt = fc3PayplanPcDdMap.get("rest_amt") != null ? fc3PayplanPcDdMap.get("rest_amt").toString() : "0";
                                                  String rest_amt_fc = fc3PayplanPcDdMap.get("rest_amt_fc") != null ? fc3PayplanPcDdMap.get("rest_amt_fc").toString() : "0";
                                                  String phid_det = fc3PayplanPcDdMap.get("phid_det") != null ? fc3PayplanPcDdMap.get("phid_det").toString() : "";
                                                  String phid = fc3PayplanPcDdMap.get("phid") != null ? fc3PayplanPcDdMap.get("phid").toString() : "";
                                                  BigDecimal rest_amt_big = new BigDecimal(rest_amt);
                                                  BigDecimal rest_amt_fc_big = new BigDecimal(rest_amt_fc);
                                                  rest_amt_big = rest_amt_big.subtract(approveAmtFcDecimal);
                                                  rest_amt_fc_big = rest_amt_fc_big.subtract(approveAmtFcDecimal);
                                                  jdbcTemplate.update("UPDATE fc3_payplan_pc_dd set rest_amt = ? , rest_amt_fc = ? , realpay_amt = ? , realpay_amt_fc = ? where phid = ?",rest_amt_big, rest_amt_fc_big, approveAmtFcDecimal, approveAmtFcDecimal, phid);

                                                  List<Map<String, Object>> fc3PayplanPcD = jdbcTemplate.queryForList(
                                                        "select phid,realpay_amt,realpay_amt_fc,phid_sourceid from fc3_payplan_pc_d where phid='" + phid_det + "'"
                                                  );
                                                  if (CollectionUtil.isNotEmpty(fc3PayplanPcD)) {
                                                       for (Map<String, Object> fc3PayplanPcDMap : fc3PayplanPcD) {
                                                           phid = fc3PayplanPcDMap.get("phid") != null ? fc3PayplanPcDMap.get("phid").toString() : "";
                                                           jdbcTemplate.update("UPDATE fc3_payplan_pc_d set realpay_amt = ? , realpay_amt_fc = ? where phid = ?", approveAmtFcDecimal, approveAmtFcDecimal, phid);
                                                       }
                                                  }
                                            }
                                       }
                                  }
                           }
                       }
                 }
                 jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ? where bill_code = ?", 2, capExRequest.getBillNo());
             }
           // jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ? , appprove_amt_fc = ? where bill_code = ?", capExRequest.getPayStatus(), capExRequest.getAppproveAmtFc(), capExRequest.getBillNo());
        } else if ("otherPay".equals(capExRequest.getCapExType())) {
            if (capExRequest.getPayStatus() == 0 ) {
                //失败 更新支付状态
                jdbcTemplate.update("UPDATE fc3_otherpay_pc set pay_status = ? where bill_code = ?", capExRequest.getPayStatus(), capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 1 ) {
                //成功 判断明细表金额是否相同 相同状态是1 不相同状态是2
                jdbcTemplate.update("UPDATE fc3_otherpay_pc set pay_status = ? where bill_code = ?", capExRequest.getPayStatus(), capExRequest.getBillNo());
            }
          //  jdbcTemplate.update("UPDATE fc3_otherpay_pc set pay_status = ? , amt_fc = ? where bill_code = ?", capExRequest.getPayStatus(), capExRequest.getAppproveAmtFc(), capExRequest.getBillNo());
        }
        return I8ResultUtil.success("同步更新成功", "");
    }

}
