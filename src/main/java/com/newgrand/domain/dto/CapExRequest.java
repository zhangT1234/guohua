package com.newgrand.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CapExRequest {

    @NotBlank(message = "类型")
    public String capExType;

    @NotBlank(message = "单据编码不能为空")
    private String billNo;

    @NotBlank(message = "付款状态不能为空")
    private Integer payStatus;

    private List<CapExDetailRequest> detail;

}
