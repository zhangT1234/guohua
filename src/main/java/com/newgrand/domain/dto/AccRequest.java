package com.newgrand.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccRequest {

    @NotBlank(message = "代码不能为空")
    public String accCode;

    @NotBlank(message = "组织账户名称不能为空")
    private String accName;

    @NotBlank(message = "开户银行信息不能为空")
    private String phidBank;

    @NotBlank(message = "银行账号不能为空")
    private String accountNo;

    @NotBlank(message = "开户名不能为空")
    private String accountName;

    @NotBlank(message = "账户类别不能为空")
    private String accType;

    @NotBlank(message = "收支属性不能为空")
    private String incomType;

    @NotBlank(message = "开户单位不能为空")
    private String phidOpenOrg;

    @NotBlank(message = "所属组织不能为空")
    private String phidOrg;

    @NotBlank(message = "是否停用不能为空")
    private String isStop;

    private String userOfsid;

}
