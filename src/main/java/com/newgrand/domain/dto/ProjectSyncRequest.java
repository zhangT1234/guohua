package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectSyncRequest {
    @NotBlank(message = "管理组织不能为空")
    @ApiModelProperty(value = "管理组织", required = true)
    private String catPhIdEXName;

    @NotBlank(message = "项目类型不能为空")
    @ApiModelProperty(value = "项目类型", required = true)
    private String phIdTypeEXName;

    @NotBlank(message = "项目编码不能为空")
    @ApiModelProperty(value = "项目编码", required = true)
    private String pcNo;

    @NotBlank(message = "项目名称不能为空")
    @ApiModelProperty(value = "项目名称", required = true)
    private String projectName;

    @NotBlank(message = "计划开工时间不能为空")
    @ApiModelProperty(value = "计划开工时间", required = true)
    private LocalDate startDate;

    @NotBlank(message = "计划竣工时间不能为空")
    @ApiModelProperty(value = "计划竣工时间", required = true)
    private LocalDate endDate;

    @NotBlank(message = "收支方向不能为空")
    @ApiModelProperty(value = "收支方向", required = true)
    private String szfx;

    @ApiModelProperty(value = "项目金额")
    private BigDecimal approxContractFc;

    @ApiModelProperty(value = "经营模式")
    private String manageMode;

    @ApiModelProperty(value = "建设单位")
    private String phIdCompany;

    @ApiModelProperty(value = "乙方单位")
    private String phIdSgOrg;

    @ApiModelProperty(value = "是否为拌合站项目")
    private String user_bhzxm;

    @ApiModelProperty(value = "计税方式")
    private String imposeType;

    @ApiModelProperty(value = "国家")
    private String countryId;

    @ApiModelProperty(value = "币种")
    private String currType;

    @ApiModelProperty(value = "合同金额")
    private BigDecimal cntAmtFc;

    @ApiModelProperty(value = "用友项目id")
    private String user_yyid;
}
