package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "员工银行账号")
public class BipBankAcctDTO {
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("银行账号")
    private String account;

    @ApiModelProperty("联行号")
    private String linked_bank_id;
}
