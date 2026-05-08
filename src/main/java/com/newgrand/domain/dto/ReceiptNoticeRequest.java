package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Data
public class ReceiptNoticeRequest {

    @ApiModelProperty(value = "单据编号", required = true)
    private String billCode;

    @NotBlank(message = "单据名称不能为空")
    @ApiModelProperty(value = "单据名称", required = true)
    private String billName;

//    @NotBlank(message = "回款属性不能为空")
//    @ApiModelProperty(value = "回款属性", required = true)
//    private Integer recFlag;

    @NotBlank(message = "付款单位不能为空")
    @ApiModelProperty(value = "付款单位", required = true)
    private String  userOfsid;

    @NotBlank(message = "回款金额不能为空")
    @ApiModelProperty(value = "回款金额", required = true)
    private String recAmtFc;

    @NotBlank(message = "可认领金额不能为空")
    @ApiModelProperty(value = "可认领金额", required = true)
    private String takeAmtFc;

    @ApiModelProperty(value = "银行", required = true)
    private String  userYh;

    @ApiModelProperty(value = "回款日期", required = false)
    private String recDate;

}
