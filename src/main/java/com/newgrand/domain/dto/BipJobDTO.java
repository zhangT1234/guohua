package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "员工任职信息")
public class BipJobDTO {
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("任职组织")
    private String org_id;

    @ApiModelProperty("部门")
    private String dept_id;

    @ApiModelProperty("任职开始日期")
    private String begindate;
}
