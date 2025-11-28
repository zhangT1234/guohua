package com.newgrand.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CusSyncRequest {
    @ApiModelProperty(value = "单位编号", required = true)
    @NotBlank(message = "单位编号不能为空")
    @JsonProperty("compNo")
    private String compNo;

    @ApiModelProperty(value = "单位名称", required = true)
    @NotBlank(message = "单位名称不能为空")
    @JsonProperty("compName")
    private String compName;

    @ApiModelProperty(value = "单位属性", required = true)
    @NotBlank(message = "单位属性不能为空")
    @JsonProperty("personFlg")
    private String personFlg;

    @ApiModelProperty(value = "统一社会信用代码", required = true)
    @NotBlank(message = "统一社会信用代码不能为空")
    @JsonProperty("unisocialCredit")
    private String unisocialCredit;

    @ApiModelProperty("证件类型")
    @JsonProperty("cardType")
    private String cardType;

    @ApiModelProperty("证件编号")
    @JsonProperty("cardNo")
    private String cardNo;

    @ApiModelProperty("用友客户id")
    @JsonProperty("user_yyid")
    private String user_yyid;
}
