package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/8/26 17:47
 * @Description:
 */
@Data
@TableName("fg3_user")
@Builder
public class Fg3User implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId("phid")
    private String phid;

    @TableField("userno")
    private String userno;

    @TableField("username")
    private String username;

    @TableField("pwd")
    private String pwd;

    @TableField("deptid")
    private String deptid;

    @TableField("mobileno")
    private String mobileno;

    @TableField("lastloginorg")
    private String lastloginorg;

    @TableField("hrid")
    private String hrid;

    @TableField("cur_orgid")
    private String curOrgid;

    @TableField("mucpwd")
    private String mucpwd;
}
