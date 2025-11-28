package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BipPayableTableFreeChIdDTO {
    @ApiModelProperty("物料")
    private String wl;
}
