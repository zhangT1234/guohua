package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BipPayableFreeChIdDTO {
    @ApiModelProperty("i8单据号")
    private String CR002;

    @ApiModelProperty("合同ID")
    private String CZ0004;

    @ApiModelProperty("合同名称")
    private String CZ003;

    @ApiModelProperty("合同编号")
    private String ZFSK000010;

    @ApiModelProperty("支出合同")
    private String contractexpense;

    @ApiModelProperty("是否冲减")
    private Boolean ZF00023;

    @ApiModelProperty("结算类型")
    private String ZFJS0001;

    @ApiModelProperty("扣减金额")
    private BigDecimal KJJE;

    @ApiModelProperty("结算（计量）金额")
    private BigDecimal ZCJL01;
}
