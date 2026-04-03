package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.dto.BillRequest;
import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.ProjectTableModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.domain.po.HrEpmMain;
import com.newgrand.service.BillService;
import com.newgrand.service.Fg3EnterpriseService;
import com.newgrand.service.OAWorkflowService;
import com.newgrand.service.ProjectTableService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.service.mp.HrEpmMainService;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class BillServiceImpl implements BillService {

     @Autowired
     private JdbcTemplate jdbcTemplate;
     @Autowired
     private OAWorkflowService oaWorkflowService;
     @Autowired
     private HrEpmMainService hrEpmMainService;
     @Autowired
     private FgOrglistService fgOrglistService;
     @Autowired
     private ProjectTableService projectTableService;
     @Autowired
     private Fg3EnterpriseService fg3EnterpriseService;

     private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${i8.outip}")
    private String i8outip;

     @Override
     public I8ReturnModel payBillOaWorkflow(Long id) {
        log.info("查询付款单单据数据》〉》〉》〉》id:" + id);
        List<Map<String, Object>> billM = jdbcTemplate.queryForList(
                "SELECT phid,bill_code,bill_name,phid_apply_psn,apply_date,phid_org,phid_apply_org,phid_apply_dept,phid_fcur,exch_rate,rec_bank_acc_no,phid_rec_bank,remark,user_sftsoa,appprove_amt_fc,phid_recent_gys FROM fc3_pay_bill WHERE phid = '" + id + "'"
        );
        if (CollectionUtil.isNotEmpty(billM)) {
            if (billM.get(0).get("user_sftsoa")!=null) {
                 if (!"是".equals(billM.get(0).get("user_sftsoa").toString())) {
                     log.info("该单据id没有开启推送oa  user_sftsoa:" + billM.get(0).get("user_sftsoa").toString() + " id:" + id);
                     return I8ResultUtil.success("该单据id没有开启推送oa");
                 }
            } else {
                log.info("该单据id没有开启推送oa  user_sftsoa为null id:" + id);
                return I8ResultUtil.success("该单据id没有开启推送oa");
            }
            String phid = billM.get(0).get("phid")!=null?billM.get(0).get("phid").toString():"";
            String billCode = billM.get(0).get("bill_code")!=null?billM.get(0).get("bill_code").toString():"";
            String billName = billM.get(0).get("bill_name")!=null?billM.get(0).get("bill_name").toString():"";
            String phidApplyPsn = billM.get(0).get("phid_apply_psn")!=null?billM.get(0).get("phid_apply_psn").toString():"";
            String applyDate = billM.get(0).get("apply_date")!=null?billM.get(0).get("apply_date").toString():"";
            String phidOrg = billM.get(0).get("phid_org")!=null?billM.get(0).get("phid_org").toString():"";
            String phidApplyOrg = billM.get(0).get("phid_apply_org")!=null?billM.get(0).get("phid_apply_org").toString():"";
            String phidApplyDept = billM.get(0).get("phid_apply_dept")!=null?billM.get(0).get("phid_apply_dept").toString():"";
            String phidFcur = billM.get(0).get("phid_fcur")!=null?billM.get(0).get("phid_fcur").toString():"";
            String exchRate = billM.get(0).get("exch_rate")!=null?((BigDecimal)billM.get(0).get("exch_rate")).toPlainString():"";
            String recBankAccNo = billM.get(0).get("rec_bank_acc_no")!=null?billM.get(0).get("rec_bank_acc_no").toString():"";
            String phidRecBank = billM.get(0).get("phid_rec_bank")!=null?billM.get(0).get("phid_rec_bank").toString():"";
            String remark = billM.get(0).get("remark")!=null?billM.get(0).get("remark").toString():"";
            String appproveAmtFc = billM.get(0).get("appprove_amt_fc")!=null?billM.get(0).get("appprove_amt_fc").toString():"";
            String phidRecentGys = billM.get(0).get("phid_recent_gys")!=null?billM.get(0).get("phid_recent_gys").toString():"";

            Map<String, Object> mainData = new HashMap<>();
            //组装主表数据
            mainData.put("djbm", billCode);
            mainData.put("djlx", "payBill");
            if (StringUtils.isNotEmpty(phidApplyPsn)) {
                LambdaQueryWrapper<HrEpmMain> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(HrEpmMain::getPhid, phidApplyPsn);
                List<HrEpmMain> list = hrEpmMainService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(list)) {
                    mainData.put("bxr", list.get(0).getCname());
                    mainData.put("sqr", list.get(0).getEmpno());
                } else {
                    mainData.put("bxr", "");
                    mainData.put("sqr", "");
                }
            } else {
                mainData.put("bxr", "");
                mainData.put("sqr", "");
            }
            if (applyDate.length()>10) {
                applyDate = applyDate.substring(0,10);
            }
            mainData.put("sqrq", applyDate);
            if (StringUtils.isNotEmpty(phidOrg)) {
                LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(FgOrglist::getPhid, phidOrg);
                List<FgOrglist> fgOrglist = fgOrglistService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(fgOrglist)) {
                    mainData.put("cwzz", fgOrglist.get(0).getUserFwId()!=null?fgOrglist.get(0).getUserFwId():"");
                    mainData.put("fycddw", fgOrglist.get(0).getUserFwId()!=null?fgOrglist.get(0).getUserFwId():"");
                    mainData.put("fycddwspyfb", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                    mainData.put("szfb", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                } else {
                    mainData.put("cwzz", "");
                    mainData.put("fycddw", "");
                    mainData.put("fycddwspyfb", "");
                    mainData.put("szfb", "");
                }
            } else {
                mainData.put("cwzz", "");
                mainData.put("fycddw", "");
                mainData.put("fycddwspyfb", "");
                mainData.put("szfb", "");
            }
//            if (StringUtils.isNotEmpty(phidApplyOrg)) {
//                LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.in(FgOrglist::getPhid, phidApplyOrg);
//                List<FgOrglist> fgOrglist = fgOrglistService.list(queryWrapper);
//                if (CollectionUtil.isNotEmpty(fgOrglist)) {
//                    mainData.put("fycddw", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
//                    mainData.put("fycddwspyfb", fgOrglist.get(0).getUserFwId()!=null?fgOrglist.get(0).getUserFwId():"");
//                } else {
//                    mainData.put("fycddw", "");
//                    mainData.put("fycddwspyfb", "");
//                }
//            } else {
//                mainData.put("fycddw", "");
//                mainData.put("fycddwspyfb", "");
//            }
            if (StringUtils.isNotEmpty(phidApplyDept)) {
                LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(FgOrglist::getPhid, phidApplyDept);
                List<FgOrglist> fgOrglist = fgOrglistService.list(queryWrapper);
                if (CollectionUtil.isNotEmpty(fgOrglist)) {
                    mainData.put("szbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                    mainData.put("fycdbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                    mainData.put("bxrbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                } else {
                    mainData.put("szbm", "");
                    mainData.put("fycdbm", "");
                    mainData.put("bxrbm", "");
                }
            } else {
                mainData.put("szbm", "");
                mainData.put("fycdbm", "");
                mainData.put("bxrbm", "");
            }

            if (StringUtils.isNotEmpty(phidRecentGys)) {
               LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
               queryWrapper.eq(Fg3Enterprise::getPhid, phidRecentGys);
               List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
               if (CollectionUtil.isNotEmpty(list)) {
                   mainData.put("sdr", list.get(0).getCompName());
               } else {
                   mainData.put("sdr", "");
               }
            } else {
                mainData.put("sdr", "");
            }
//            if (StringUtils.isNotEmpty(phidFcur)) {
//                List<Map<String, Object>> fcur = jdbcTemplate.queryForList(
//                        "SELECT phid,fc_code FROM fg_fcur WHERE phid='" + phidFcur + "'"
//                );
//                if (CollectionUtil.isNotEmpty(fcur)) {
//                    String fcCode = fcur.get(0).get("fc_code")!=null?fcur.get(0).get("fc_code").toString():"";
//                    mainData.put("bz", fcCode);
//                } else {
//                    mainData.put("bz", "");
//                }
//            } else {
//                mainData.put("bz", "");
//            }
            mainData.put("bz", "1002Z0100000000001K1");
            mainData.put("hl", exchRate);
            mainData.put("bxsy", billName);
            mainData.put("hjbxje", appproveAmtFc);
            mainData.put("fycdbm", "302");
            mainData.put("szbm", "301");
            mainData.put("bxrbm", "301");
            mainData.put("sqr", "103");
            mainData.put("fycdbmzdydx", "302");

            mainData.put("cbzxbm", "1004");
            mainData.put("xjllxm", "0001A1100000000008UN");
            mainData.put("ncjsfs", "3");
            mainData.put("yhzh", "1001A110000000000BA1");
            mainData.put("cklx", "1001A110000000001HKU");
            mainData.put("yhlx", "0001Z010000000000366");

            if (StringUtils.isNotEmpty(phidRecBank)) {
                List<Map<String, Object>> bank = jdbcTemplate.queryForList(
                        "SELECT phid,bankname FROM fg_bank WHERE phid='" + phidRecBank + "'"
                );
                if (CollectionUtil.isNotEmpty(bank)) {
                    String bankName = bank.get(0).get("bankname")!=null?bank.get(0).get("bankname").toString():"";
                    mainData.put("skyh", bankName);
                } else {
                    mainData.put("skyh", "");
                }
            } else {
                mainData.put("skyh", "");
            }
            mainData.put("skyhzh", recBankAccNo);

            List<Map<String, Object>> detailData = new ArrayList<>();
            String requestName = billName + "发起流程";
           // String workflowId = "419";   //测试
            //String workflowId = "1117";   //正式
          //  String workflowId = "1099";   //正式
            String workflowId = "1120";
            //String userId = "1";
            String userId = "103";

            //明细数据
            List<Map<String, Object>> billD = jdbcTemplate.queryForList(
                    "SELECT phid,phid_mst,ng_insert_dt,phid_proj,ac_code,apply_amt_fc,apply_amt FROM fc3_pay_bill_det WHERE phid_mst= '" + phid + "'"
            );
            if (CollectionUtil.isNotEmpty(billD)) {
                for (Map<String, Object> map : billD) {
                    Map<String, Object> det = new HashMap<>();
                    String dt = map.get("ng_insert_dt")!=null?map.get("ng_insert_dt").toString():"";
                    String proj = map.get("phid_proj")!=null?map.get("phid_proj").toString():"";
                    String acCode = map.get("ac_code")!=null?map.get("ac_code").toString():"";
                   // String supply = map.get("phid_supply")!=null?map.get("phid_supply").toString():"";
                    String applyAmtFc = map.get("apply_amt_fc")!=null?map.get("apply_amt_fc").toString():"";
                    String applyAmt = map.get("apply_amt")!=null?map.get("apply_amt").toString():"";
                    String xzdid = map.get("phid")!=null?map.get("phid").toString():"";

                    det.put("xzdid", xzdid);
                    if (dt.length()>10) {
                        dt = dt.substring(0,10);
                    }
                    det.put("fyfsrq", dt);
//                    if (StringUtils.isNotEmpty(proj)) {
//                        LambdaQueryWrapper<ProjectTableModel> queryWrapper = new LambdaQueryWrapper<>();
//                        queryWrapper.eq(ProjectTableModel::getPhid, proj);
//                        List<ProjectTableModel> list = projectTableService.list(queryWrapper);
//                        if (CollectionUtil.isNotEmpty(list)) {
//                            det.put("xm", list.get(0).getPcNo());
//                        }
//
//                    }
                  //  det.put("cbkm", acCode);
//                    if (StringUtils.isNotEmpty(supply)) {
//                        LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
//                        queryWrapper.eq(Fg3Enterprise::getPhid, supply);
//                        List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
//                        if (CollectionUtil.isNotEmpty(list)) {
//                            det.put("gys", list.get(0).getUserOfsid()!=null?list.get(0).getUserOfsid():"");
//                        }
//                    }
                    det.put("pjzs", "");
                    det.put("je", applyAmtFc!=null?applyAmtFc:"");
                    det.put("bbje", applyAmt!=null?applyAmt:"");
                    det.put("xm", "1826");
//                    if (StringUtils.isNotEmpty(phidRecBank)) {
//                        List<Map<String, Object>> bank = jdbcTemplate.queryForList(
//                                "SELECT phid,bankname FROM fg_bank WHERE phid='" + phidRecBank + "'"
//                        );
//                        if (CollectionUtil.isNotEmpty(bank)) {
//                            String bankName = bank.get(0).get("bankname")!=null?bank.get(0).get("bankname").toString():"";
//                            det.put("skyh", bankName);
//                        }
//                    }
//                    det.put("skyhzh", recBankAccNo);
                    detailData.add(det);
                }
            }
            //获取附件
            String attachSql = "select phid,asr_guid,asr_fid,asr_id,asr_name,asr_fill,asr_fillname,asr_code,asr_table from attachment_record where asr_attach_table = 'c_pfc_attachment' and asr_table = 'fc3_pay_bill' and asr_code = '" + phid + "'";
            List<Map<String, Object>> attachInfos = jdbcTemplate.queryForList(attachSql);
            List<Map<String, Object>> attachList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(attachInfos)) {
                for (Map<String, Object> map : attachInfos) {
                    String asrTable = map.get("asr_table")!=null?map.get("asr_table").toString():"";
                    String asrFid = map.get("asr_fid")!=null?map.get("asr_fid").toString():"";
                    String asrName = map.get("asr_name")!=null?map.get("asr_name").toString():"";
                    String asrFid3 = asrFid.substring(0,3);
                    String fileUrl = i8outip + "/JFileSrv/previewFiles/" + asrFid3 + "/" + asrFid + "/source/" + asrName + "?isapp=1&notExistFileReload=1&asrTable=" + asrTable + "&asrCode=" + phid + "&fileName=" + asrName + "&dbToken=0001&compressPic=0";
                    Map<String, Object> attachInfo = new HashMap<>();
                    attachInfo.put("filePath", fileUrl);
                    attachInfo.put("fileName", asrName);
                    attachList.add(attachInfo);
                }
            }
            mainData.put("fj", attachList);
                //发送OA流程
            log.info("准备发送OA流程》〉》〉》〉》id:" + id);
            try {
                String result = oaWorkflowService.createWorkflow(requestName, workflowId, mainData, detailData, userId);
                if (result != null) {
                    if (result.contains("\"code\":\"SUCCESS\"")) {
                        log.info("发送OA流程返回结果成功》〉》〉》〉》id:" + id + result);
                        jdbcTemplate.update("UPDATE fc3_pay_bill set user_yts = ? where phid = ?", 1, id);
                        return I8ResultUtil.success(result);
                    } else {
                        log.info("发送OA流程返回结果失败》〉》〉》〉》id:" + id + result);
                        return I8ResultUtil.error(result);
                    }
                } else {
                    log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "发送OA流程失败");
                    return I8ResultUtil.error("发送OA流程失败");
                }
            } catch (Exception e) {
                log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "发送OA流程异常：" + e.getMessage());
                return I8ResultUtil.error("发送OA流程异常：" + e.getMessage());
            }
        } else {
            log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "该单据id不存在付款单数据");
            return I8ResultUtil.error("该单据id不存在付款单数据");
        }
     }

     @Override
     public I8ReturnModel otherPayOaWorkflow(Long id){
         log.info("其他项目单单据数据》〉》〉》〉》id:" + id);
         List<Map<String, Object>> billM = jdbcTemplate.queryForList(
                 "SELECT phid,bill_code,bill_name,phid_employee,bill_date,phid_org,phid_dept,phid_fcur,exch_rate,phid_rec_bank,rec_bank_acc,remark,user_sftsoa,phid_rec_ent FROM fc3_otherpay_pc WHERE phid = '" + id + "'"
         );
         if (CollectionUtil.isNotEmpty(billM)) {
             if (billM.get(0).get("user_sftsoa")!=null) {
                 if (!"是".equals(billM.get(0).get("user_sftsoa").toString())) {
                     log.info("该单据id没有开启推送oa  user_sftsoa:" + billM.get(0).get("user_sftsoa").toString() + "id:" + id);
                     return I8ResultUtil.success("该单据id没有开启推送oa");
                 }
             } else {
                 log.info("该单据id没有开启推送oa  user_sftsoa为null id:" + id);
                 return I8ResultUtil.success("该单据id没有开启推送oa");
             }
             String phid = billM.get(0).get("phid")!=null?billM.get(0).get("phid").toString():"";
             String billCode = billM.get(0).get("bill_code")!=null?billM.get(0).get("bill_code").toString():"";
             String billName = billM.get(0).get("bill_name")!=null?billM.get(0).get("bill_name").toString():"";
             String phidEmployee = billM.get(0).get("phid_employee")!=null?billM.get(0).get("phid_employee").toString():"";
             String billDate = billM.get(0).get("bill_date")!=null?billM.get(0).get("bill_date").toString():"";
             String phidOrg = billM.get(0).get("phid_org")!=null?billM.get(0).get("phid_org").toString():"";
             String phidDept = billM.get(0).get("phid_dept")!=null?billM.get(0).get("phid_dept").toString():"";
             String phidFcur = billM.get(0).get("phid_fcur")!=null?billM.get(0).get("phid_fcur").toString():"";
             String exchRate = billM.get(0).get("exch_rate")!=null?((BigDecimal)billM.get(0).get("exch_rate")).toPlainString():"";
             String phidRecBank = billM.get(0).get("phid_rec_bank")!=null?billM.get(0).get("phid_rec_bank").toString():"";
             String recBankAcc = billM.get(0).get("rec_bank_acc")!=null?billM.get(0).get("rec_bank_acc").toString():"";
             String remark = billM.get(0).get("remark")!=null?billM.get(0).get("remark").toString():"";
             String phidRecEnt = billM.get(0).get("phid_rec_ent")!=null?billM.get(0).get("phid_rec_ent").toString():"";


             Map<String, Object> mainData = new HashMap<>();
             //组装主表数据
             mainData.put("djbm", billCode);
             mainData.put("djlx", "otherPay");
             if (StringUtils.isNotEmpty(phidEmployee)) {
                 LambdaQueryWrapper<HrEpmMain> queryWrapper = new LambdaQueryWrapper<>();
                 queryWrapper.eq(HrEpmMain::getPhid, phidEmployee);
                 List<HrEpmMain> list = hrEpmMainService.list(queryWrapper);
                 if (CollectionUtil.isNotEmpty(list)) {
                     mainData.put("bxr", list.get(0).getCname());
                     mainData.put("sqr", list.get(0).getEmpno());
                 } else {
                     mainData.put("bxr", "");
                     mainData.put("sqr", "");
                 }
             } else {
                 mainData.put("bxr", "");
                 mainData.put("sqr", "");
             }
             if (billDate.length()>10) {
                 billDate = billDate.substring(0,10);
             }
             mainData.put("sqrq", billDate);
             if (StringUtils.isNotEmpty(phidOrg)) {
                 LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
                 queryWrapper.in(FgOrglist::getPhid, phidOrg);
                 List<FgOrglist> fgOrglist = fgOrglistService.list(queryWrapper);
                 if (CollectionUtil.isNotEmpty(fgOrglist)) {
                     mainData.put("cwzz", fgOrglist.get(0).getUserFwId()!=null?fgOrglist.get(0).getUserFwId():"");
                     mainData.put("fycddw", fgOrglist.get(0).getUserFwId()!=null?fgOrglist.get(0).getUserFwId():"");
                     mainData.put("fycddwspyfb", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                     mainData.put("szfb", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                 } else {
                     mainData.put("cwzz", "");
                     mainData.put("fycddw", "");
                     mainData.put("fycddwspyfb", "");
                     mainData.put("szfb", "");
                 }
             } else {
                 mainData.put("cwzz", "");
                 mainData.put("fycddw", "");
                 mainData.put("fycddwspyfb", "");
                 mainData.put("szfb", "");
             }
             if (StringUtils.isNotEmpty(phidDept)) {
                 LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
                 queryWrapper.in(FgOrglist::getPhid, phidDept);
                 List<FgOrglist> fgOrglist = fgOrglistService.list(queryWrapper);
                 if (CollectionUtil.isNotEmpty(fgOrglist)) {
                     mainData.put("szbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                     mainData.put("fycdbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                     mainData.put("bxrbm", fgOrglist.get(0).getUserOfsid()!=null?fgOrglist.get(0).getUserOfsid():"");
                 } else {
                     mainData.put("szbm", "");
                     mainData.put("fycdbm", "");
                     mainData.put("bxrbm", "");
                 }
             } else {
                 mainData.put("szbm", "");
                 mainData.put("fycdbm", "");
                 mainData.put("bxrbm", "");
             }
             if (StringUtils.isNotEmpty(phidRecEnt)) {
                 LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
                 queryWrapper.eq(Fg3Enterprise::getPhid, phidRecEnt);
                 List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
                 if (CollectionUtil.isNotEmpty(list)) {
                     mainData.put("sdr", list.get(0).getCompName());
                 } else {
                     mainData.put("sdr", "");
                 }
             } else {
                 mainData.put("sdr", "");
             }
//             if (StringUtils.isNotEmpty(phidFcur)) {
//                 List<Map<String, Object>> fcur = jdbcTemplate.queryForList(
//                         "SELECT phid,fc_code FROM fg_fcur WHERE phid='" + phidFcur + "'"
//                 );
//                 if (CollectionUtil.isNotEmpty(fcur)) {
//                     String fcCode = fcur.get(0).get("fc_code")!=null?fcur.get(0).get("fc_code").toString():"";
//                     mainData.put("bz", fcCode);
//                 } else {
//                     mainData.put("bz", "");
//                 }
//             } else {
//                 mainData.put("bz", "");
//             }
             mainData.put("bz", "1002Z0100000000001K1");
             mainData.put("hl", exchRate);
             mainData.put("bxsy", billName);
             mainData.put("fycdbm", "302");
             mainData.put("szbm", "301");
             mainData.put("bxrbm", "301");
             mainData.put("sqr", "103");
             mainData.put("fycdbmzdydx", "302");

             mainData.put("cbzxbm", "1004");
             mainData.put("xjllxm", "0001A1100000000008UN");
             mainData.put("ncjsfs", "3");
             mainData.put("yhzh", "1001A110000000000BA1");
             mainData.put("cklx", "1001A110000000001HKU");
             mainData.put("yhlx", "0001Z010000000000366");

             if (StringUtils.isNotEmpty(phidRecBank)) {
                 List<Map<String, Object>> bank = jdbcTemplate.queryForList(
                         "SELECT phid,bankname FROM fg_bank WHERE phid='" + phidRecBank + "'"
                 );
                 if (CollectionUtil.isNotEmpty(bank)) {
                     String bankName = bank.get(0).get("bankname")!=null?bank.get(0).get("bankname").toString():"";
                     mainData.put("skyh", bankName);
                 } else {
                     mainData.put("skyh", "");
                 }
             } else {
                 mainData.put("skyh", "");
             }
             mainData.put("skyhzh", recBankAcc);


             List<Map<String, Object>> detailData = new ArrayList<>();
             String requestName = billName + "发起流程";
             //String workflowId = "419";   //测试
             //String workflowId = "1117";   //正式
            // String workflowId = "1099";   //正式
             String workflowId = "1120";
             //String userId = "1";
             String userId = "103";

             //明细数据
             List<Map<String, Object>> billD = jdbcTemplate.queryForList(
                     "SELECT phid,phid_mst,ng_insert_dt,haveamt_fc,haveamt FROM fc3_otherpay_pc_d WHERE phid_mst= '" + phid + "'"
             );
             if (CollectionUtil.isNotEmpty(billD)) {
                 for (Map<String, Object> map : billD) {
                     Map<String, Object> det = new HashMap<>();
                     String dt = map.get("ng_insert_dt")!=null?map.get("ng_insert_dt").toString():"";
                     String haveamtFc = map.get("haveamt_fc")!=null?map.get("haveamt_fc").toString():"";
                     String haveamt = map.get("haveamt")!=null?map.get("haveamt").toString():"";
                     String xzdid = map.get("phid")!=null?map.get("phid").toString():"";

                     det.put("xzdid", xzdid);
                     if (dt.length()>10) {
                         dt = dt.substring(0,10);
                     }
                     det.put("fyfsrq", dt);
                     det.put("pjzs", "");
                     det.put("je", haveamtFc);
                     det.put("bbje", haveamt);
                     det.put("xm", "1826");
//                     if (StringUtils.isNotEmpty(phidRecBank)) {
//                         List<Map<String, Object>> bank = jdbcTemplate.queryForList(
//                                 "SELECT phid,bankname FROM fg_bank WHERE phid='" + phidRecBank + "'"
//                         );
//                         if (CollectionUtil.isNotEmpty(bank)) {
//                             String bankName = bank.get(0).get("bankname")!=null?bank.get(0).get("bankname").toString():"";
//                             det.put("skyh", bankName);
//                         }
//                     }
//                     det.put("skyhzh", recBankAcc);
                      detailData.add(det);
                 }
             }
             //获取附件
             String attachSql = "select phid,asr_guid,asr_fid,asr_id,asr_name,asr_fill,asr_fillname,asr_code,asr_table from attachment_record where asr_attach_table = 'c_pfc_attachment' and asr_table = 'fc3_otherpay_pc' and asr_code = '" + phid + "'";
             List<Map<String, Object>> attachInfos = jdbcTemplate.queryForList(attachSql);
             List<Map<String, Object>> attachList = new ArrayList<>();
             if (CollectionUtil.isNotEmpty(attachInfos)) {
                 for (Map<String, Object> map : attachInfos) {
                     String asrTable = map.get("asr_table")!=null?map.get("asr_table").toString():"";
                     String asrFid = map.get("asr_fid")!=null?map.get("asr_fid").toString():"";
                     String asrName = map.get("asr_name")!=null?map.get("asr_name").toString():"";
                     String asrFid3 = asrFid.substring(0,3);
                     String fileUrl = i8outip + "/JFileSrv/previewFiles/" + asrFid3 + "/" + asrFid + "/source/" + asrName + "?isapp=1&notExistFileReload=1&asrTable=" + asrTable + "&asrCode=" + phid + "&fileName=" + asrName + "&dbToken=0001&compressPic=0";
                     Map<String, Object> attachInfo = new HashMap<>();
                     attachInfo.put("filePath", fileUrl);
                     attachInfo.put("fileName", asrName);
                     attachList.add(attachInfo);
                 }
             }
             mainData.put("fj", attachList);
             //发送OA流程
             log.info("准备发送OA流程》〉》〉》〉》id:" + id);
             try {
                 String result = oaWorkflowService.createWorkflow(requestName, workflowId, mainData, detailData, userId);
                 if (result != null) {
                     if (result.contains("\"code\":\"SUCCESS\"")) {
                         jdbcTemplate.update("UPDATE fc3_otherpay_pc set user_yts = ? where phid = ?", 1, id);
                         log.info("发送OA流程返回结果成功》〉》〉》〉》id:" + id + result);
                         return I8ResultUtil.success(result);
                     } else {
                         log.info("发送OA流程返回结果失败》〉》〉》〉》id:" + id + result);
                         return I8ResultUtil.error(result);
                     }
                 } else {
                     log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "发送OA流程失败");
                     return I8ResultUtil.error("发送OA流程失败");
                 }
             } catch (Exception e) {
                 log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "发送OA流程异常：" + e.getMessage());
                 return I8ResultUtil.error("发送OA流程异常：" + e.getMessage());
             }
         } else {
             log.info("发送OA流程返回结果》〉》〉》〉》id:" + id + "该单据id不存在付款单数据");
             return I8ResultUtil.error("该单据id不存在付款单数据");
         }
     }


    /**
     * 定时推送
     */
    @Scheduled(fixedDelay = 180 * 1000, initialDelay = 60 * 1000)
    public void pushOAWorkFlow() {
        log.info("执行定时推送OA！！！！！");
        String sql = "select * from fc3_pay_bill where user_sftsoa = '是' and user_yts != 1 and pay_status != 2 and check_flag = 1";
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
        if (CollectionUtil.isNotEmpty(data)) {
            for (Map<String, Object> map : data) {
                String phid = map.get("phid")!=null?map.get("phid").toString():"";
                log.info("付款单定时推送OA！！！！！ 单据id:" + phid);
                if (StringUtils.isNotEmpty(phid)) {
                    payBillOaWorkflow(Long.parseLong(phid));
                }
            }
        }

        String othepaysql = "select * from fc3_otherpay_pc where user_sftsoa = '是' and user_yts != 1 and pay_status != 1 and check_flag = 1";
        List<Map<String, Object>> otherdata = jdbcTemplate.queryForList(othepaysql);
        if (CollectionUtil.isNotEmpty(otherdata)) {
            for (Map<String, Object> map : otherdata) {
                String phid = map.get("phid")!=null?map.get("phid").toString():"";
                log.info("其他项目支出单定时推送OA！！！！！ 单据id:" + phid);
                if (StringUtils.isNotEmpty(phid)) {
                    otherPayOaWorkflow(Long.parseLong(phid));
                }
            }
        }
    }

}
