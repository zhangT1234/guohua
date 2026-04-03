package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("project_table")
public class ProjectTableModel {
    @TableField("pc_no")
    private String pcNo;

    @TableField("project_name")
    private String projectName;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("szfx")
    private String szfx;

    @TableField("phid_type")
    private Long phidType;

    @TableField("cat_phid")
    private String catPhid;

    @TableField("approx_contract_fc")
    private BigDecimal approxContractFc;

    @TableField("manage_mode")
    private String manageMode;

    @TableField("phid_company")
    private String phidCompany;

    @TableField("phid_sg_org")
    private String phidSgOrg;

//    @TableField("user_bhzxm")
//    private String userBhzxm;

    @TableField("imposetype")
    private String imposeType;

    @TableField("countryid")
    private String countryId;

    @TableField("curr_type")
    private String currType;

    @TableField("cnt_amt_fc")
    private String cntAmtFc;

//    @TableField("user_yyid")
//    private String userYyid;

    @TableField("phid_fi_ocode")
    private String phidFiOcode;

    @TableField("project_org")
    private String projectOrg;

    @TableField("phid")
    private String phid;
}
