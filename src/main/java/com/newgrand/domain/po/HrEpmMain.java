package com.newgrand.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("hr_epm_main")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrEpmMain implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("cno")
    private String cno;

    @TableField("cname")
    private String cname;

    @TableField("sexno")
    private String sexno;

    @TableField("dept")
    private String dept;

    @TableField("cboo")
    private String cboo;

    @TableField("emptype")
    private String emptype;

    @TableField("cardno")
    private String cardno;

    @TableField("cardtype")
    private String cardtype;

    @TableField("empstatus")
    private String empstatus;

    @TableField(value = "mobileno", exist = false)
    private String mobileno;

    @TableField("empno")
    private String empno;
}
