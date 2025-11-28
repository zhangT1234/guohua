package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BipfreeChIdDTO {
    @ApiModelProperty("i8单据号")
    private String CR002;

    @ApiModelProperty("合同ID")
    private String CZ0004;

    @ApiModelProperty("合同名称")
    private String CZ003;

    @ApiModelProperty("是否总承包")
    private String CZSR;

    @ApiModelProperty("是否产值预提单")
    private Boolean ZFCZ00020;

    @ApiModelProperty("合同编号")
    private String ZFSK000010;

    @ApiModelProperty("计税方式")
    private String ZFCZ0003;

    @ApiModelProperty("材料差额")
    private String ZFJL000040;

    @ApiModelProperty("预收款抵扣")
    private BigDecimal ZFJL000030;

    @ApiModelProperty("收入合同")
    private String contractincome;

    @ApiModelProperty("业务类型")
    private String ZFSK0002;
}
