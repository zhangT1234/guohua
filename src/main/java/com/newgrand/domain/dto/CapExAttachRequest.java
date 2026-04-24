package com.newgrand.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CapExAttachRequest {

    @NotBlank(message = "类型")
    public String capExType;

    @NotBlank(message = "单据编码不能为空")
    private String billNo;

    @NotBlank(message = "附件url不能为空")
    private String fileUrl;

}
