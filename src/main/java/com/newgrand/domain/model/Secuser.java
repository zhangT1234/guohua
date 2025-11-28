package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2023/6/3 22:22
 * @Description: TODO
 */
@Data
@TableName("secuser")
@Builder
public class Secuser implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("logid")
    private String logid;

    @TableField("pwd")
    private String pwd;

    @TableField("deptno")
    private String deptno;

    @TableField("lastloginorg")
    private String lastloginorg;
}
