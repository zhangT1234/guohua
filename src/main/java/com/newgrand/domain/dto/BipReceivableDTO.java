package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BipReceivableDTO {
    @ApiModelProperty("保证请求幂等，全局唯一，不超过32位")
    private String resubmitCheckKey;

    @ApiModelProperty("单据日期")
    private String billDate;

    @ApiModelProperty("交易类型编码")
    private String bustype;

    @ApiModelProperty("业务组织")
    private String org;

    @ApiModelProperty("往来对象类型，1客户，2员工，3资金业务对象")
    private String objectType;

    @ApiModelProperty("客户")
    private String customer;

    @ApiModelProperty("员工")
    private String employee;

    @ApiModelProperty("员工编码")
    private String employeeCode;

    @ApiModelProperty("汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty("项目")
    private String project;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("方向")
    private String direction;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("表头特征组")
    private BipfreeChIdDTO freeChId;

    @ApiModelProperty("表体数据")
    private List<BipReceivableTableDTO> bodyItem;

    @ApiModelProperty("发票类型")
    private String invoiceType;
}
