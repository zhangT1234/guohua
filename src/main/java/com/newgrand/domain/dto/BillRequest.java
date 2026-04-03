package com.newgrand.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BillRequest {

    @NotBlank(message = "单据编码不能为空")
    private String billNo;

}
