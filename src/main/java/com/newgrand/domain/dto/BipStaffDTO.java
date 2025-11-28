package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BipStaffDTO {
    @ApiModelProperty("保证请求幂等，全局唯一，不超过32位")
    private String resubmitCheckKey;

    @ApiModelProperty("员工表主键ID，新增时无，更新时必填")
    private String id;

    @ApiModelProperty("员工编码")
    private String code;

    @ApiModelProperty("员工姓名")
    private String name;

    /*@ApiModelProperty("证件类型ID")
    private String cert_type;

    @ApiModelProperty("证件类型名称")
    private String cert_type_name;*/

    @ApiModelProperty("证件号")
    private String cert_no;

    @ApiModelProperty("性别，1男，2女，0不限")
    private String sex;

    @ApiModelProperty("出生日期，格式 yyyy-MM-dd")
    private String birthdate;

    @ApiModelProperty("手机号，86表示中国")
    private String mobile;

    @ApiModelProperty("员工状态，0：初始态、1：已启用、2：已停用，示例：1")
    private Integer enable = 1;

    @ApiModelProperty("员工任职信息")
    private List<BipJobDTO> mainJobList;

    @ApiModelProperty("员工兼职信息")
    private List<BipJobDTO> ptJobList;

    @ApiModelProperty("员工银行账号")
    private List<BipBankAcctDTO> bankAcctList;
}
