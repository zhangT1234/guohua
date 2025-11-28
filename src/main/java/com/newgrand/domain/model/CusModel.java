package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("fg_customfile")
public class CusModel {
    @TableField("compno")
    private String compNo;

    @TableField("compname")
    private String compName;

    @TableField("person_flg")
    private String personFlg;

    @TableField("unisocialcredit")
    private String unisocialCredit;

    @TableField("cardtype")
    private String cardType;

    @TableField("cardno")
    private String cardNo;

    @TableField("user_yyid")
    private String userYyid;
}
