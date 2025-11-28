package com.newgrand.domain.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EmpModel {

    @NotBlank(message = "员工编码不能为空")
    @JsonProperty("cNo")
    public String cNo;

    @NotBlank(message = "员工姓名不能为空")
    @JsonProperty("cName")
    public String cName;

    @NotBlank(message = "员工性别不能为空")
    @Pattern(regexp = "^[12]$", message = "员工性别必须为1(男)或2(女)")
    @JsonProperty("sexNo")
    public String sexNo;

    @NotBlank(message = "证件类型不能为空")
    @JsonProperty("cardType")
    public String cardType;

    @NotBlank(message = "证件号码不能为空")
    @JsonProperty("cardNo")
    public String cardNo;

    @NotBlank(message = "出生日期不能为空")
    @JsonProperty("birthday")
    public String birthday;

    @NotBlank(message = "部门编号不能为空")
    @JsonProperty("dept")
    public String dept;

    @NotBlank(message = "员工状态不能为空")
    @JsonProperty("empStatus")
    public String empStatus;

    @NotBlank(message = "手机号码不能为空")
    @JsonProperty("mobile")
    public String mobile;

    @NotBlank(message = "入职时间不能为空")
    @JsonProperty("cdt")
    public String cdt;
}
