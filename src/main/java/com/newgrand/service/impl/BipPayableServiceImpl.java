package com.newgrand.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.dto.*;
import com.newgrand.service.BipPayableService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BipPayableServiceImpl implements BipPayableService {
    private final BipRequestUtil bipRequestUtil;
    private final JdbcTemplate jdbcTemplate;
    private final GetPhIdHelper getPhIdHelper;
    private final UipLog dbLog;

    private final Map<String, String> CNT_TYPE = new HashMap<String, String>(){{
       put("劳务分包", "劳务分包");
       put("专业分包", "专业分包");
       put("机械设备租赁合同", "设备租赁");
       put("其它支出合同", "其他支出");
       put("大宗材料采购合同", "大宗材料采购");
       put("固定资产采购合同", "设备采购");
    }};

    @Override
    public BipResult testSyncPayable(BipRequest<BipPayableDTO> data) {
        try {
            data.getData().setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
            BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/payable/save", JSONObject.toJSONString(data));
            log.info(resultData.toString());
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult labor(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.*,cnt.bill_no cnt_no,cnt.title cnt_title,pct.name cnt_type_name from pcm3_cnt_pay_m m left join pcm3_cnt_m cnt on m.phid_cnt = cnt.phid left join pcm3_cnt_type pct on pct.phid = cnt.cnt_type"
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipPayableDTO data = new BipPayableDTO();
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
                data.setBustype("支出结算单");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("phid_ocode")), "user_ofsid"));
                // 往来对象类型，0供应商，1客户，2员工，3资金业务对象
                data.setObjectType("0");
                // 供应商
                String supplier = getPhIdHelper.GetValueByphid("fg3_enterprise", StringHelper.nullToEmpty(bill.get("rec_comp_name")), "user_yyid2");
                supplier = supplier == null ? "" : supplier;
                data.setSupplier(supplier.replace("C", "").replace("S", ""));
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("creator")), "cno"));
                // 汇率
                data.setExchangeRate(new BigDecimal(1));
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("phid_pc")), "user_yyid"));
                // 备注
                String remarks = StringHelper.nullToEmpty(bill.get("remarks"));
                String user_kjjeyy = StringHelper.nullToEmpty(bill.get("user_kjjeyy"));
                if("".equals(remarks)) {
                    remarks = user_kjjeyy;
                } else {
                    if(!"".equals(user_kjjeyy)) {
                        remarks = "备注：" + remarks + " / 扣减原因：" + user_kjjeyy;
                    }
                }
                data.setRemarks(remarks);
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
                BipPayableFreeChIdDTO freeChId = new BipPayableFreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 支出合同
                freeChId.setContractexpense(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 合同名称
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                // 合同编号
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 是否冲减
                freeChId.setZF00023(null);
                // 结算类型
                freeChId.setZFJS0001(CNT_TYPE.get(StringHelper.nullToEmpty(bill.get("cnt_type_name"))));
                // 扣减金额
                freeChId.setKJJE(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_bqkjje"))));
                // 结算（计量）金额
                freeChId.setZCJL01(DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(bill.get("user_jsjlje"))));

                data.setFreeChId(freeChId);

                List<BipPayableTableDTO> tableDatas = new ArrayList<>();

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

                    BipPayableTableDTO tableData = new BipPayableTableDTO();
                    BipPayableTableFreeChIdDTO tableFreeChIdDTO = new BipPayableTableFreeChIdDTO();
                    tableFreeChIdDTO.setWl(StringHelper.nullToEmpty(billD.get("item_no")) + "-" + StringHelper.nullToEmpty(billD.get("item_name")) + "-" + StringHelper.nullToEmpty(billD.get("spec")) + "-" + StringHelper.nullToEmpty(billD.get("unit")));
                    tableData.setFreeChId(tableFreeChIdDTO);
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

                BipRequest<BipPayableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncPayable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE pcm3_cnt_pay_m set user_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BipResult other(String billNo) {
        return null;
    }

    @Override
    public BipResult purchase(String billNo) {
        return null;
    }

    @Override
    public BipResult check(String billNo) {
        try {
            List<Map<String, Object>> bills = jdbcTemplate.queryForList(
                    " select m.* from p_form0000000034_m m "
                            + " where m.bill_no = '" + billNo + "' "
            );
            BipPayableDTO data = new BipPayableDTO();
            if (!bills.isEmpty()) {
                Map<String, Object> bill = bills.get(0);

                if(!"".equals(StringHelper.nullToEmpty(bill.get("user_bip_no")))) {
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
                data.setBustype("盘点单");
                // 业务组织
                data.setOrg(getPhIdHelper.GetValueByphid("fg_orglist", StringHelper.nullToEmpty(bill.get("ocode")), "user_ofsid"));
                // 往来对象类型，0供应商，1客户，2员工，3资金业务对象
                data.setObjectType("2");
                // 供应商
                data.setSupplier("");
                // 员工
                data.setEmployee(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("fillpsn")), "user_bip_no"));
                // 员工编码
                data.setEmployeeCode(getPhIdHelper.GetValueByphid("hr_epm_main", StringHelper.nullToEmpty(bill.get("fillpsn")), "cno"));
                // 汇率
                data.setExchangeRate(new BigDecimal(1));
                // 项目
                data.setProject(getPhIdHelper.GetValueByphid("project_table", StringHelper.nullToEmpty(bill.get("pc")), "user_yyid"));
                // 备注
                data.setRemarks(StringHelper.nullToEmpty(bill.get("remarks")));
                // 状态
                data.setStatus("0");
                // 合同编号
                data.setContractNo(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 方向
//                data.setDirection("1");

                // 表头特征组
                BipPayableFreeChIdDTO freeChId = new BipPayableFreeChIdDTO();
                // i8单据号
                freeChId.setCR002(StringHelper.nullToEmpty(bill.get("bill_no")));
                // 合同ID
                freeChId.setCZ0004(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 支出合同
                freeChId.setContractexpense(StringHelper.nullToEmpty(bill.get("phid_cnt")));
                // 合同名称
                freeChId.setCZ003(StringHelper.nullToEmpty(bill.get("cnt_title")));
                // 合同编号
                freeChId.setZFSK000010(StringHelper.nullToEmpty(bill.get("cnt_no")));
                // 是否冲减
                freeChId.setZF00023("是".equals(StringHelper.nullToEmpty(bill.get("ddlbcol_1"))) ? true : false);
                // 结算类型
                freeChId.setZFJS0001("盘点单");

                data.setFreeChId(freeChId);

                List<BipPayableTableDTO> tableDatas = new ArrayList<>();

                // 查询子表
                List<Map<String, Object>> billDs = jdbcTemplate.queryForList(
                        " select d.*,cbs.cbs_code from p_form0000000034_d d left join bd_cbs cbs on d.cbs = cbs.phid"
                                + " where d.m_code = '" + StringHelper.nullToEmpty(bill.get("phid")) + "' "
                );
                for(Map<String, Object> billD: billDs) {
                    BigDecimal amtVatFc = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("hsje"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxrate = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("tax")));
                    BigDecimal taxamt = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("tax_money"))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal oriTaxExcludedAmount = DecimalUtils.stringToBigDecimal(StringHelper.nullToEmpty(billD.get("amt"))).setScale(2, RoundingMode.HALF_UP);

                    BipPayableTableDTO tableData = new BipPayableTableDTO();
                    BipPayableTableFreeChIdDTO tableFreeChIdDTO = new BipPayableTableFreeChIdDTO();
                    tableFreeChIdDTO.setWl(StringHelper.nullToEmpty(billD.get("user_itemcode")) + "-" + StringHelper.nullToEmpty(billD.get("user_itemname")) + "-" + StringHelper.nullToEmpty(billD.get("user_spec")) + "-" + StringHelper.nullToEmpty(billD.get("phid_msunit")));
                    tableData.setFreeChId(tableFreeChIdDTO);
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

                BipRequest<BipPayableDTO> param = new BipRequest<>();
                param.setData(data);
                BipResult resultData = syncPayable(param);
                if("200".equals(resultData.getCode())) {
                    jdbcTemplate.update("UPDATE p_form0000000034_m set u_bip_no = ? where phid = ?", ((JSONObject)resultData.getData()).getString("code"), bill.get("phid"));
                }
                return resultData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BipResult syncPayable(BipRequest<BipPayableDTO> data) {
        try {
            data.getData().setResubmitCheckKey(RandomStringUtils.randomAlphanumeric(32));
            System.out.println("data:" + JSONObject.toJSONString(data));
            dbLog.info("payable", "应付单请求参数", JSONObject.toJSONString(data));
            BipResult resultData = bipRequestUtil.sendPost("/iuap-api-gateway/vh8c6ypa/current_yonbip_default_sys/kekai/payable/save", JSONObject.toJSONString(data));
            log.info(resultData.toString());
            dbLog.info("payable", "应付单返回值", resultData.toString());
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
