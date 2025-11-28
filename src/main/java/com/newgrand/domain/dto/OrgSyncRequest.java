package com.newgrand.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class OrgSyncRequest {

    @NotBlank(message = "组织编码不能为空")
    @ApiModelProperty(value = "组织编码", required = true)
    @JsonProperty("oCode")
    private String oCode;

    @NotBlank(message = "组织名称不能为空")
    @ApiModelProperty(value = "组织名称", required = true)
    @JsonProperty("oName")
    private String oName;

    @NotBlank(message = "组织类型不能为空")
    @Pattern(regexp = "^[YN]$", message = "组织类型只能是Y或N")
    @ApiModelProperty(value = "组织类型", required = true)
    @JsonProperty("orgType")
    private String orgType;

    @JsonProperty("parentOrg")
    @ApiModelProperty("归属组织")
    private String parentOrg;

    @NotBlank(message = "用友组织id不能为空")
    @ApiModelProperty(value = "用友组织id", required = true)
    private String user_yyzzid;

    @ApiModelProperty(value = "负责人")
    private String empcode;

    @ApiModelProperty(value = "分管领导")
    private String otax;
}
