package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BipReceivableTableDTO {
    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("发票号")
    private String invoiceNo;

    @ApiModelProperty("费用项目")
    private String expenseItemCode;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("原币金额")
    private BigDecimal oriTaxIncludedAmount;

    @ApiModelProperty("税率")
    private BigDecimal taxRate;

    @ApiModelProperty("税额")
    private BigDecimal oriTaxAmount;

    @ApiModelProperty("无税金额")
    private BigDecimal oriTaxExcludedAmount;
}
