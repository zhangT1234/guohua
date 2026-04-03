package com.newgrand.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateActiveRequest {

    @NotBlank(message = "第三方组织id不能为空")
    @ApiModelProperty(value = "第三方组织id", required = true)
    @JsonProperty("id")
    private String id;

    @NotBlank(message = "停用状态0停用1启用")
    @ApiModelProperty(value = "停用状态0停用1启用", required = true)
    @JsonProperty("value")
    private String value;

}
