package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemResRequest {

    @NotBlank(message = "资源分类编码")
    @ApiModelProperty(value = "资源分类编码", required = true)
    private String code;

    @NotBlank(message = "资源分类名称")
    @ApiModelProperty(value = "资源分类名称", required = true)
    private String name;

    @NotBlank(message = "计价方式")
    @ApiModelProperty(value = "计价方式", required = true)
    private String fid;

    @ApiModelProperty(value = "资源分类父级编码", required = true)
    private String parentCode;

    @NotBlank(message = "资源类型")
    @ApiModelProperty(value = "资源类型", required = true)
    private String resourceType;

    @ApiModelProperty("第三方id")
    private String userOfsid;

}
