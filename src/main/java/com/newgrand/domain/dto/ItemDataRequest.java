package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemDataRequest {

    @NotBlank(message = "资源主文件编码")
    @ApiModelProperty(value = "资源主文件编码", required = true)
    private String itemNo;

    @NotBlank(message = "资源主文件名称")
    @ApiModelProperty(value = "资源主文件名称", required = true)
    private String itemName;

    @NotBlank(message = "资源分类编码")
    @ApiModelProperty(value = "资源分类编码", required = true)
    private String resCode;

    @NotBlank(message = "计价方式")
    @ApiModelProperty(value = "计价方式", required = true)
    private String fid;

    @NotBlank(message = "资源类型")
    @ApiModelProperty(value = "资源类型", required = true)
    private String resourceType;

    @ApiModelProperty("第三方id")
    private String userOfsid;

    @ApiModelProperty("资源分类")
    private ItemResRequest itemResRequest;

}
