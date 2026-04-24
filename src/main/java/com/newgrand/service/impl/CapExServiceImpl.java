package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.newgrand.domain.dto.CapExAttachRequest;
import com.newgrand.domain.dto.CapExDetailRequest;
import com.newgrand.domain.dto.CapExRequest;
import com.newgrand.domain.model.FileModel;
import com.newgrand.domain.model.I8FileModel;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.AttachmentService;
import com.newgrand.service.CapExService;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CapExServiceImpl implements CapExService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private AttachmentService attachmentService;

    @Override
    public I8ReturnModel updateCapEx(CapExRequest capExRequest){
        log.info("回调接口单据编码：" + capExRequest.getBillNo());
        if  ("payBill".equals(capExRequest.getCapExType())) {
             if (capExRequest.getPayStatus() == 0 ) {
                 //失败 更新支付状态 3
                 jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ?, user_yts = ? where bill_code = ?", 3, 3, capExRequest.getBillNo());
             } else if (capExRequest.getPayStatus() == 1 ) {
                 //成功 更新支付状态 2
                 List<Map<String, Object>> fc3PayBill = jdbcTemplate.queryForList(
                         "SELECT phid,appprove_amt_fc,phid_sourcemid FROM fc3_pay_bill where bill_code = '" + capExRequest.getBillNo() + "'"
                 );
                 log.info("回调接口单据查询数据：" + JSON.toJSONString(fc3PayBill));
                 String phidSourcemId = fc3PayBill.get(0).get("phid_sourcemid")!=null?fc3PayBill.get(0).get("phid_sourcemid").toString():"";
                 String appproveAmtFc = fc3PayBill.get(0).get("appprove_amt_fc")!=null?fc3PayBill.get(0).get("appprove_amt_fc").toString():"0";
                 String mainPhid = fc3PayBill.get(0).get("phid")!=null?fc3PayBill.get(0).get("phid").toString():"";
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
//                       for (CapExDetailRequest capExDetailRequest : list) {
                           //明细数据
                           List<Map<String, Object>> fc3PayBillDet = jdbcTemplate.queryForList(
                                   "select phid,phid_mst,phid_proj,ac_code,apply_amt_fc,phid_sourcemid,phid_sourceid,approve_amt_fc FROM fc3_pay_bill_det WHERE phid_mst= '" + mainPhid + "'"
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
//                       }
                 }
                 jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ?, user_yts = ? where bill_code = ?", 2, 1, capExRequest.getBillNo());
             } else if (capExRequest.getPayStatus() == 2 ) {
                 jdbcTemplate.update("UPDATE fc3_pay_bill set user_yts = ? where bill_code = ?",  6, capExRequest.getBillNo());
             }
           // jdbcTemplate.update("UPDATE fc3_pay_bill set pay_status = ? , appprove_amt_fc = ? where bill_code = ?", capExRequest.getPayStatus(), capExRequest.getAppproveAmtFc(), capExRequest.getBillNo());
        } else if ("otherPay".equals(capExRequest.getCapExType())) {
            if (capExRequest.getPayStatus() == 0 ) {
                //失败 更新支付状态
                jdbcTemplate.update("UPDATE fc3_otherpay_pc set pay_status = ?, user_yts = ? where bill_code = ?", capExRequest.getPayStatus(), 3, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 1 ) {
                //成功 判断明细表金额是否相同 相同状态是1 不相同状态是2
                jdbcTemplate.update("UPDATE fc3_otherpay_pc set pay_status = ?, user_yts = ? where bill_code = ?", capExRequest.getPayStatus(), 1, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 2 ) {
                jdbcTemplate.update("UPDATE fc3_otherpay_pc set user_yts = ? where bill_code = ?", 6, capExRequest.getBillNo());
            }
        } else if ("tendPay".equals(capExRequest.getCapExType())) {
            if (capExRequest.getPayStatus() == 0 ) {
                //失败 更新支付状态
                jdbcTemplate.update("UPDATE crm3_tend_pay set user_yts = ? where bill_no = ?", 3, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 1 ) {
                //成功
                jdbcTemplate.update("UPDATE crm3_tend_pay set state = ?, user_yts = ? where bill_no = ?", 2, 1, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 2 ) {
                jdbcTemplate.update("UPDATE crm3_tend_pay set user_yts = ? where bill_no = ?", 6, capExRequest.getBillNo());
            }
        } else if ("guaranteePay".equals(capExRequest.getCapExType())) {
            if (capExRequest.getPayStatus() == 0 ) {
                //失败 更新支付状态
                jdbcTemplate.update("UPDATE p_form_tendguarantee set user_yts = ? where bill_no = ?", 3, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 1 ) {
                //成功
                jdbcTemplate.update("UPDATE p_form_tendguarantee set u_status = ?, user_yts = ? where bill_no = ?", capExRequest.getPayStatus(), 1, capExRequest.getBillNo());
            } else if (capExRequest.getPayStatus() == 2 ) {
                jdbcTemplate.update("UPDATE p_form_tendguarantee set user_yts = ? where bill_no = ?", 6, capExRequest.getBillNo());
            }
        }
        return I8ResultUtil.success("同步更新成功", "");
    }

    @Override
    public I8ReturnModel updateAttachment(CapExAttachRequest capExAttachRequest){
        log.info("回调接口单据编码：" + capExAttachRequest.getBillNo());
        if  ("payBill".equals(capExAttachRequest.getCapExType())) {
            List<Map<String, Object>> payBill = jdbcTemplate.queryForList(
                    "SELECT phid FROM fc3_pay_bill where bill_code = '" + capExAttachRequest.getBillNo() + "'"
            );
            if (CollectionUtil.isNotEmpty(payBill)) {
                String phid = payBill.get(0).get("phid") != null ? payBill.get(0).get("phid").toString() : null;
                if (phid != null) {
                    upLoadFile(phid, "fc3_pay_bill", capExAttachRequest.getFileUrl());
                }
            }
        } else if ("otherPay".equals(capExAttachRequest.getCapExType())) {
            List<Map<String, Object>> otherPay = jdbcTemplate.queryForList(
                    "SELECT phid FROM fc3_otherpay_pc where bill_code = '" + capExAttachRequest.getBillNo() + "'"
            );
            if (CollectionUtil.isNotEmpty(otherPay)) {
                String phid = otherPay.get(0).get("phid") != null ? otherPay.get(0).get("phid").toString() : null;
                if (phid != null) {
                    upLoadFile(phid, "fc3_otherpay_pc", capExAttachRequest.getFileUrl());
                }
            }
        } else if ("tendPay".equals(capExAttachRequest.getCapExType())) {
            List<Map<String, Object>> tendPay = jdbcTemplate.queryForList(
                    "SELECT phid FROM crm3_tend_pay where bill_code = '" + capExAttachRequest.getBillNo() + "'"
            );
            if (CollectionUtil.isNotEmpty(tendPay)) {
                String phid = tendPay.get(0).get("phid") != null ? tendPay.get(0).get("phid").toString() : null;
                if (phid != null) {
                    upLoadFile(phid, "crm3_tend_pay", capExAttachRequest.getFileUrl());
                }
            }
        } else if ("guaranteePay".equals(capExAttachRequest.getCapExType())) {
            List<Map<String, Object>> tendguaranteePay = jdbcTemplate.queryForList(
                    "SELECT phid FROM p_form_tendguarantee where bill_code = '" + capExAttachRequest.getBillNo() + "'"
            );
            if (CollectionUtil.isNotEmpty(tendguaranteePay)) {
                String phid = tendguaranteePay.get(0).get("phid") != null ? tendguaranteePay.get(0).get("phid").toString() : null;
                if (phid != null) {
                    upLoadFile(phid, "p_form_tendguarantee", capExAttachRequest.getFileUrl());
                }
            }
        }
        return I8ResultUtil.success("同步更新附件成功", "");
    }

    public void upLoadFile(String attachmentId, String asrtable, String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        String dirPath = "D:\\guohua\\file\\" + attachmentId;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
        }
        String targetFile = "D:\\guohua\\file\\" + attachmentId + "\\" + fileName;
        HttpURLConnection connection = null;
        try{
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            log.info("文件下载完成: " + targetFile);
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }
        //附件上传
        uploadAttachmentInfo(attachmentId, asrtable, fileName, targetFile);
    }


    //上传附件
    public void uploadAttachmentInfo(String asrCode, String asrTable, String fileName, String filePath){
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            I8FileModel data = new I8FileModel();
            UUID uuid = UUID.randomUUID();
            data.setAsr_session_guid(uuid.toString());
            data.setAsr_attach_table("c_pfc_attachment");
            data.setAsr_table(asrTable);
            data.setAsr_code(asrCode);
            data.setAsr_fillname("asr_name=" + fileName + "&asr_fill=315211029000006&asr_fillname=9997");
            data.setAsr_data(bytes);
            String base64Encoded = Base64.getEncoder().encodeToString(bytes);
            data.setAsr_data_base64(base64Encoded);
            boolean result = attachmentService.upLoadFile(data);
            log.info("回调附件上传结果：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis!=null) {
                try {
                    fis.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
