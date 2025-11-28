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
@TableName("fg_orglist")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgOrglist implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("parent_orgid")
    private String parentOrgid;

    @TableField("ocode")
    private String ocode;

    @TableField("oname")
    private String oname;

    @TableField("bopomofo")
    private String bopomofo;

    @TableField("codevalue")
    private String codevalue;

    @TableField("ifcorp")
    private String ifcorp;

    @TableField("isactive")
    private String isactive;

    @TableField("orgtype")
    private String orgtype;

    @TableField("ng_update_dt")
    private String ngUpdateDt;

    @TableField("user_yyzzid")
    private String userYyzzid;

    @TableField("empcode")
    private String empcode;

    @TableField("otax")
    private String otax;

}
