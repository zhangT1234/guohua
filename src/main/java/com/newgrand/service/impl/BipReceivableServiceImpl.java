package com.newgrand.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.domain.dto.*;
import com.newgrand.service.BipReceivableService;
import com.newgrand.utils.BigDecimalUtil;
import com.newgrand.utils.BipRequestUtil;
import com.newgrand.utils.i8util.GetPhIdHelper;
import com.newgrand.utils.i8util.StringHelper;
import com.newgrand.utils.security.DecimalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BipReceivableServiceImpl implements BipReceivableService {
    private final BipRequestUtil bipRequestUtil;
    private final JdbcTemplate jdbcTemplate;
    private final GetPhIdHelper getPhIdHelper;
    private final UipLog dbLog;

    @Override
    public BipResult testSyncReceivable(BipRequest<BipReceivableDTO> data) {
        try {
            data.getData().setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
            BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/receivable/save", JSONObject.toJSONString(data));
            log.info(resultData.toString());
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult factCz(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.*,cnt.bill_no cnt_no,cnt.title cnt_title from pms3_cz_act_m m left join pcm3_cnt_m cnt on m.user_user_zbht = cnt.phid "
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipReceivableDTO data = new BipReceivableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                if(!"".equals(StringHelper.nullToEmpty(bill.get("user_bip_no")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据已推送过，不允许重复推送");
                    return result;
                }

                if(!"1".equals(StringHelper.nullToEmpty(bill.get("chk_flg")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据未审批，不允许推送");
                    return result;
                }

                data.setBillDate(StringHelper.nullToEmpty(bill.get("bill_dt")));
                data.setBustype("产值单");
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "user_yyzzid"));
                data.setObjectType("1");
                data.setCustomer(getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(bill.get("user_khmc")), "user_yyid").replace("C", "").replace("S", ""));
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "user_bip_no"));
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "cno"));

                data.setExchangeRate(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("exch_rate"))));
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("phid_pc")), "user_yyid"));
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                data.setStatus("0");
                data.setContractNo(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 发票类型
                String invoiceType = StringHelper.nullToEmpty(bill.get("user_jlfplx"));
                data.setInvoiceType(invoiceType.equals("01") ? "3100" : (invoiceType.equals("02") ? "3200" : (invoiceType.equals("03") ? "0" : "")));
//                data.setDirection("1");
                BipfreeChIdDTO freeChId = new BipfreeChIdDTO();
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("user_user_zbht")));
                // 收入合同
                freeChId.setContractincome(StringHelper.nullToEmpty(bill.get("user_user_zbht")));
                freeChId.setZFSK0002("工程款");
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                freeChId.setCZSR(StringHelper.nullToEmpty(bill.get("user_sfzcb")));
                freeChId.setZFCZ00020("是".equals(StringHelper.nullToEmpty(bill.get("user_sfczytd"))) ? true : false);
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                freeChId.setZFCZ0003("一般计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "02" : ("简易计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "01" : ""));

                data.setFreeChId(freeChId);
                List<BipReceivableTableDTO> tableDatas = new ArrayList<>();

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select cbs.cbs_code,d.* from pms3_cz_act_d d left join bd_cbs cbs on d.phid_cbs = cbs.phid "
                                + " where d.pphid = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                BigDecimal taxRate = BigDecimalUtil.toSpecial(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_sl"))));
                for(Map<String, Object> billD: billDs) {
                    BigDecimal amtVatFc = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("con_amt"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal amt = amtVatFc.divide(new BigDecimal(1).add(taxRate.divide(new BigDecimal(100))), 2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxAmount = amtVatFc.subtract(amt);

                    BipReceivableTableDTO tableData = new BipReceivableTableDTO();
                    // 合同编号
                    tableData.setContractNo("");
                    // 发票号
                    tableData.setInvoiceNo("");
                    // 费用项目
                    tableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("cbs_code")));
                    // 备注
                    tableData.setRemarks(StringHelper.nullToEmpty(billD.get("remarks")));
                    // 原币金额
                    tableData.setOriTaxIncludedAmount(amtVatFc);
                    // 税率
                    tableData.setTaxRate(taxRate);
                    // 税额
                    tableData.setOriTaxAmount(oriTaxAmount);
                    // 无税金额
                    tableData.setOriTaxExcludedAmount(amt);

                    tableDatas.add(tableData);
                }

                data.setBodyItem(tableDatas);

                BipRequest<BipReceivableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncReceivable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE pms3_cz_act_m set user_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult otherCz(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select * from p_form0000000067_m m "
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipReceivableDTO data = new BipReceivableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                if(!"".equals(StringHelper.nullToEmpty(bill.get("u_bip_no")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据已推送过，不允许重复推送");
                    return result;
                }

                if(!"1".equals(StringHelper.nullToEmpty(bill.get("ischeck")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据未审批，不允许推送");
                    return result;
                }

                // 单据日期
                data.setBillDate(StringHelper.nullToEmpty(bill.get("bill_dt")));
                // 交易类型编码
                data.setBustype("产值单");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("ocode")), "user_yyzzid"));
                // 往来对象类型，1客户，2员工，3资金业务对象
                data.setObjectType("1");
                // 客户
                data.setCustomer("");
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("fillpsn")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("fillpsn")), "cno"));
                // 汇率
                data.setExchangeRate(null);
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("pc")), "user_yyid"));
                // 备注
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                // 状态
                data.setStatus("0");
                // 合同编号
                data.setContractNo("");
                // 发票类型
                String invoiceType = StringHelper.nullToEmpty(bill.get("user_jlfplx"));
                data.setInvoiceType(invoiceType.equals("01") ? "3100" : (invoiceType.equals("02") ? "3200" : (invoiceType.equals("03") ? "0" : "")));
                // 方向
//                data.setDirection("1");

                // 表头特征组
                BipfreeChIdDTO freeChId = new BipfreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004("");
                // 收入合同
                freeChId.setContractincome("");
                // 合同名称
                freeChId.setCZ003("");
                // 是否总承包
                freeChId.setCZSR("否");
                // 是否产值预提单
                freeChId.setZFCZ00020(null);
                // 合同编号
                freeChId.setZFSK000010("");
                // 计税方式
                freeChId.setZFCZ0003("");

                data.setFreeChId(freeChId);

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select d.*,cnt.bill_no,cbs.cbs_code,cnt.bill_no cnt_no from p_form0000000067_d1 d left join pcm3_cnt_m cnt on d.userhelp_1 = cnt.phid left join bd_cbs cbs on d.userhelp_2 = cbs.phid"
                                + " where d.m_code = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                for(Map<String, Object> billD: billDs) {
                    if("".equals(StringHelper.nullToEmpty(billD.get("userhelp_2")))) {
                        continue;
                    }
                    List<BipReceivableTableDTO> tableDatas = new ArrayList<>();
                    BigDecimal oriTaxIncludedAmount = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("numericcol_1"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal longcol_1 = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("longcol_1")));
                    BigDecimal numericcol_3 = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("numericcol_3"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxAmount = oriTaxIncludedAmount.subtract(numericcol_3).setScale(2, RoundingMode.HALF_UP);

                    BipReceivableTableDTO tableData = new BipReceivableTableDTO();
                    // 合同编号
                    tableData.setContractNo(StringHelper.nullToEmpty(billD.get("cnt_no")));
                    // 发票号
                    tableData.setInvoiceNo("");
                    // 费用项目
                    tableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("cbs_code")));
                    // 备注
                    tableData.setRemarks("");
                    // 原币金额
                    tableData.setOriTaxIncludedAmount(oriTaxIncludedAmount);
                    // 税率
                    tableData.setTaxRate(BigDecimalUtil.toSpecial(longcol_1));
                    // 税额
                    tableData.setOriTaxAmount(oriTaxAmount);
                    // 无税金额
                    tableData.setOriTaxExcludedAmount(numericcol_3);

                    tableDatas.add(tableData);

                    data.setBodyItem(tableDatas);

                    data.getFreeChId().setCR002(StringHelper.nullToEmpty(billD.get("phid")));

                    data.setCustomer(getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(billD.get("u_comp_no")), "user_yyid").replace("C", "").replace("S", ""));

                    BipRequest<BipReceivableDTO> param = new BipRequest<>();
                    param.setData(data);
                    BipResult resultData = syncReceivable(param);
                    if("200".equals(resultData.getCode())) {
                        jdbcTemplate.update("UPDATE p_form0000000067_d1 set u_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), billD.get("phid"));
                    } else {
                        return resultData;
                    }
                }
                BipResult resultData = BipResult.builder().build();
                resultData.setCode("200");
                resultData.setMessage("同步成功");
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 弃用
    @Override
    @Deprecated
    public BipResult otherSettlement(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.*,cnt.bill_no cnt_no,cnt.title cnt_title from pcm3_cnt_pay_m m left join pcm3_cnt_m cnt on m.phid_cnt = cnt.phid"
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipReceivableDTO data = new BipReceivableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                // 单据日期
                data.setBillDate(StringHelper.nullToEmpty(bill.get("bill_dt")));
                // 交易类型编码
                data.setBustype("产值单");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "user_yyzzid"));
                // 往来对象类型，1客户，2员工，3资金业务对象
                data.setObjectType("1");
                // 客户
                data.setCustomer(getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(bill.get("rec_comp_name")), "user_yyid").replace("C", "").replace("S", ""));
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "cno"));
                // 汇率
                data.setExchangeRate(null);
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("phid_pc")), "user_yyid"));
                // 备注
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                // 状态
                data.setStatus("0");
                // 发票类型
                String invoiceType = StringHelper.nullToEmpty(bill.get("user_jlfplx"));
                data.setInvoiceType(invoiceType.equals("01") ? "3100" : (invoiceType.equals("02") ? "3200" : (invoiceType.equals("03") ? "0" : "")));
                // 方向
//                data.setDirection("1");

                // 表头特征组
                BipfreeChIdDTO freeChId = new BipfreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 收入合同
                freeChId.setContractincome(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 合同名称
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                // 是否总承包
                freeChId.setCZSR(StringHelper.nullToEmpty(bill.get("user_sfzcb")));
                // 是否产值预提单
                freeChId.setZFCZ00020(null);
                // 合同编号
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 计税方式
                freeChId.setZFCZ0003("");
                // 材料差额
                freeChId.setZFJL000040(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_clce"))).setScale(2, RoundingMode.HALF_UP).toString());

                data.setFreeChId(freeChId);

                List<BipReceivableTableDTO> tableDatas = new ArrayList<>();

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select d.*,cnt.bill_no cnt_no from pcm3_cnt_pay_d d left join pcm3_cnt_m cnt on d.phid_cnt = cnt.phid"
                                + " where d.pphid = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                for(Map<String, Object> billD: billDs) {
                    BigDecimal amtVatFc = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("amt_vat_fc"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxrate = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxrate")));
                    BigDecimal taxamt = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxamt"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxExcludedAmount = amtVatFc.subtract(taxamt).setScale(2, RoundingMode.HALF_UP);

                    BipReceivableTableDTO tableData = new BipReceivableTableDTO();
                    // 合同编号
                    tableData.setContractNo(StringHelper.nullToEmpty(billD.get("cnt_no")));
                    // 发票号
                    tableData.setInvoiceNo("");
                    // 费用项目
                    tableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("phid_cbs")));
                    // 备注
                    tableData.setRemarks(StringHelper.nullToEmpty(billD.get("remarks")));
                    // 原币金额
                    tableData.setOriTaxIncludedAmount(amtVatFc);
                    // 税率
                    tableData.setTaxRate(BigDecimalUtil.toSpecial(taxrate));
                    // 税额
                    tableData.setOriTaxAmount(taxamt);
                    // 无税金额
                    tableData.setOriTaxExcludedAmount(oriTaxExcludedAmount);

                    tableDatas.add(tableData);
                }
                data.setBodyItem(tableDatas);

                BipRequest<BipReceivableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncReceivable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE pcm3_cnt_pay_d set user_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult contractSettlementJl(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.*,cnt.bill_no cnt_no,cnt.title cnt_title from pcm3_cnt_pay_m m left join pcm3_cnt_m cnt on m.phid_cnt = cnt.phid"
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipReceivableDTO data = new BipReceivableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                if(!"".equals(StringHelper.nullToEmpty(bill.get("user_bip_no")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据已推送过，不允许重复推送");
                    return result;
                }

                if(!"1".equals(StringHelper.nullToEmpty(bill.get("chk_flg")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据未审批，不允许推送");
                    return result;
                }

                // 单据日期
                data.setBillDate(StringHelper.nullToEmpty(bill.get("bill_dt")));
                // 交易类型编码
                data.setBustype("合同结算");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "user_yyzzid"));
                // 往来对象类型，1客户，2员工，3资金业务对象
                data.setObjectType("1");
                // 客户
                String customer = getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(bill.get("rec_comp_name")), "user_yyid");
                customer = customer == null ? "" : customer;
                data.setCustomer(customer.replace("C", "").replace("S", ""));
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "cno"));
                // 汇率
                data.setExchangeRate(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("exch_rate"))));
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("phid_pc")), "user_yyid"));
                // 备注
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                // 状态
                data.setStatus("0");
                // 合同编号
                data.setContractNo(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 发票类型
                String invoiceType = StringHelper.nullToEmpty(bill.get("user_jlfplx"));
                data.setInvoiceType(invoiceType.equals("01") ? "3100" : (invoiceType.equals("02") ? "3200" : (invoiceType.equals("03") ? "0" : "")));
                // 方向
//                data.setDirection("1");

                // 表头特征组
                BipfreeChIdDTO freeChId = new BipfreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 收入合同
                freeChId.setContractincome(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 合同名称
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                // 是否总承包
                freeChId.setCZSR(StringHelper.nullToEmpty(bill.get("user_sfzcb")));
                // 是否产值预提单
                freeChId.setZFCZ00020(null);
                // 预收款抵扣
                freeChId.setZFJL000030(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_yskdk"))));
                // 合同编号
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 计税方式
                freeChId.setZFCZ0003("一般计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "02" : ("简易计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "01" : ""));
                // 材料差额
                freeChId.setZFJL000040(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_clce"))).setScale(2, RoundingMode.HALF_UP).toString());

                data.setFreeChId(freeChId);

                List<BipReceivableTableDTO> tableDatas = new ArrayList<>();

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select d.*,cnt.bill_no cnt_no,cbs.cbs_code from pcm3_cnt_pay_d d left join pcm3_cnt_m cnt on d.phid_cnt = cnt.phid left join bd_cbs cbs on d.phid_cbs = cbs.phid"
                                + " where d.pphid = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                BipReceivableTableDTO tmpTableData = null;
                for(Map<String, Object> billD: billDs) {
                    BigDecimal amtVatFc = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("amt_vat_fc"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxrate = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxrate")));
                    BigDecimal taxamt = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxamt"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxExcludedAmount = amtVatFc.subtract(taxamt).setScale(2, RoundingMode.HALF_UP);

                    BipReceivableTableDTO tableData = new BipReceivableTableDTO();
                    // 合同编号
                    tableData.setContractNo(StringHelper.nullToEmpty(billD.get("cnt_no")));
                    // 发票号
                    tableData.setInvoiceNo("");
                    // 费用项目
                    tableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("cbs_code")));
                    // 备注
                    tableData.setRemarks(StringHelper.nullToEmpty(billD.get("remarks")));
                    // 原币金额
                    tableData.setOriTaxIncludedAmount(amtVatFc);
                    // 税率
                    tableData.setTaxRate(BigDecimalUtil.toSpecial(taxrate));
                    // 税额
                    tableData.setOriTaxAmount(taxamt);
                    // 无税金额
                    tableData.setOriTaxExcludedAmount(oriTaxExcludedAmount);

                    if(tmpTableData == null) {
                        tmpTableData = new BipReceivableTableDTO();
                        // 合同编号
                        tmpTableData.setContractNo(StringHelper.nullToEmpty(billD.get("cnt_no")));
                        // 发票号
                        tmpTableData.setInvoiceNo("");
                        // 费用项目
                        tmpTableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("cbs_code")));
                        // 备注
                        tmpTableData.setRemarks(StringHelper.nullToEmpty(billD.get("remarks")));
                        // 原币金额
                        tmpTableData.setOriTaxIncludedAmount(amtVatFc);
                        // 税率
                        tmpTableData.setTaxRate(taxrate);
                        // 税额
                        tmpTableData.setOriTaxAmount(taxamt);
                        // 无税金额
                        tmpTableData.setOriTaxExcludedAmount(oriTaxExcludedAmount);
                    }

                    tableDatas.add(tableData);
                }
                data.setBodyItem(tableDatas);

                BipRequest<BipReceivableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncReceivable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE pcm3_cnt_pay_m set user_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                    if(!"".equals(StringHelper.nullToEmpty(bill.get("user_clce"))) && !"0.00".equals(StringHelper.nullToEmpty(bill.get("user_clce")))) {
                        tmpTableData.setOriTaxIncludedAmount(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_clce"))).setScale(2, RoundingMode.HALF_UP));
                        BigDecimal taxrate = tmpTableData.getTaxRate();
                        BigDecimal oriTaxExcludedAmount = tmpTableData.getOriTaxIncludedAmount().divide(new BigDecimal(1).add(taxrate), 2, RoundingMode.HALF_UP);
                        BigDecimal taxamt = tmpTableData.getOriTaxIncludedAmount().subtract(oriTaxExcludedAmount);
                        tmpTableData.setTaxRate(BigDecimalUtil.toSpecial(taxrate));
                        tmpTableData.setOriTaxAmount(taxamt);
                        tmpTableData.setOriTaxExcludedAmount(oriTaxExcludedAmount);
                        data.setBodyItem(Arrays.asList(tmpTableData));
                        param.getData().setBustype("材料差额");
                        BipResult anotherResultData = syncReceivable(param);
                        if("200".equals(anotherResultData.getCode())) {
                            jdbcTemplate.update("UPDATE pcm3_cnt_pay_m set user_bip_no1 = ? where phid = ?", ((JSONObject)anotherResultData.getData()).getString("code"), bill.get("phid"));
                        }
                    }
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult otherSettlementJl(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.*,cnt.bill_no cnt_no,cnt.title cnt_title from pcm3_cnt_pay_m m left join pcm3_cnt_m cnt on m.phid_cnt = cnt.phid"
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipReceivableDTO data = new BipReceivableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                if(!"".equals(StringHelper.nullToEmpty(bill.get("user_bip_no")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据已推送过，不允许重复推送");
                    return result;
                }

                if(!"1".equals(StringHelper.nullToEmpty(bill.get("chk_flg")))) {
                    BipResult result = BipResult.builder().build();
                    result.setCode("500");
                    result.setMessage("该单据未审批，不允许推送");
                    return result;
                }

                // 单据日期
                data.setBillDate(StringHelper.nullToEmpty(bill.get("bill_dt")));
                // 交易类型编码
                data.setBustype("合同结算");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "user_yyzzid"));
                // 往来对象类型，1客户，2员工，3资金业务对象
                data.setObjectType("1");
                // 客户
                data.setCustomer(getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(bill.get("rec_comp_name")), "user_yyid").replace("C", "").replace("S", ""));
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "cno"));
                // 汇率
                data.setExchangeRate(null);
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("phid_pc")), "user_yyid"));
                // 备注
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                // 状态
                data.setStatus("0");
                // 合同编号
                data.setContractNo(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 发票类型
                String invoiceType = StringHelper.nullToEmpty(bill.get("user_jlfplx"));
                data.setInvoiceType(invoiceType.equals("01") ? "3100" : (invoiceType.equals("02") ? "3200" : (invoiceType.equals("03") ? "0" : "")));
                // 方向
//                data.setDirection("1");

                // 表头特征组
                BipfreeChIdDTO freeChId = new BipfreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 收入合同
                freeChId.setContractincome(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 合同名称
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                // 业务类型
                String userYwlx = StringHelper.nullToEmpty(bill.get("user_ywlx"));
                Map<String, String> ywlxMap = new HashMap<String, String>() {{
                    put("1", "加工收入");
                    put("2", "经营收入");
                    put("3", "经营租赁");
                    put("4", "销售货物");
                }};
                freeChId.setZFSK0002(ywlxMap.getOrDefault(userYwlx, null));
                // 是否总承包
                dbLog.info("normal", "otherSettlementJl", StringHelper.nullToEmpty(bill.get("user_sfzcb")));
                freeChId.setCZSR(StringHelper.nullToEmpty(bill.get("user_sfzcb")));
                // 是否产值预提单
                freeChId.setZFCZ00020(null);
                // 预收款抵扣
                freeChId.setZFJL000030(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_yskdk"))));
                // 合同编号
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 计税方式
                freeChId.setZFCZ0003("一般计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "02" : ("简易计税".equals(StringHelper.nullToEmpty(bill.get("user_jsfs"))) ? "01" : ""));

                data.setFreeChId(freeChId);

                List<BipReceivableTableDTO> tableDatas = new ArrayList<>();

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select d.*,cnt.bill_no cnt_no,cbs.cbs_code from pcm3_cnt_pay_d d left join pcm3_cnt_m cnt on d.phid_cnt = cnt.phid left join bd_cbs cbs on d.phid_cbs = cbs.phid"
                                + " where d.pphid = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                for(Map<String, Object> billD: billDs) {
                    BigDecimal amtVatFc = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("amt_vat_fc"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxrate = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxrate")));
                    BigDecimal taxamt = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("taxamt"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxExcludedAmount = amtVatFc.subtract(taxamt).setScale(2, RoundingMode.HALF_UP);

                    BipReceivableTableDTO tableData = new BipReceivableTableDTO();
                    // 合同编号
                    tableData.setContractNo(StringHelper.nullToEmpty(billD.get("cnt_no")));
                    // 发票号
                    tableData.setInvoiceNo("");
                    // 费用项目
                    tableData.setExpenseItemCode(StringHelper.nullToEmpty(billD.get("cbs_code")));
                    // 备注
                    tableData.setRemarks(StringHelper.nullToEmpty(billD.get("remarks")));
                    // 原币金额
                    tableData.setOriTaxIncludedAmount(amtVatFc);
                    // 税率
                    tableData.setTaxRate(BigDecimalUtil.toSpecial(taxrate));
                    // 税额
                    tableData.setOriTaxAmount(taxamt);
                    // 无税金额
                    tableData.setOriTaxExcludedAmount(oriTaxExcludedAmount);

                    tableDatas.add(tableData);
                }
                data.setBodyItem(tableDatas);

                BipRequest<BipReceivableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncReceivable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE pcm3_cnt_pay_m set user_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                }
                String orgCode = getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "ocode");
                System.out.println("orgCode:" + orgCode);
                // 交建业务
                if(!"".equals(orgCode) && orgCode.startsWith("0002")) {
                    param.getData().setBustype("产值单");
                    param.getData().getFreeChId().setZFCZ00020(false);
                    BipResult anotherResultData = syncReceivable(param);
                    if("200".equals(anotherResultData.getCode())) {
                        jdbcTemplate.update("UPDATE pcm3_cnt_pay_m set user_bip_no1 = ? where phid = ?", ((JSONObject)anotherResultData.getData()).getString("code"), bill.get("phid"));
                    }
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BipResult syncReceivable(BipRequest<BipReceivableDTO> param) {
        try {
            param.getData().setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
            System.out.println("data:" + JSONObject.toJSONString(param));
            dbLog.info("receivable", "应收单请求参数", JSONObject.toJSONString(param));
            BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/receivable/save", JSONObject.toJSONString(param));
            System.out.println(resultData.toString());
            dbLog.info("receivable", "应收单返回值", resultData.toString());
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
