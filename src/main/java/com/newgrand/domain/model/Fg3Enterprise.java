package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 11:25
 * @Description: TODO
 */
@Data
@TableName("fg3_enterprise")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fg3Enterprise implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @TableField("user_ofsid")
    private String userOfsid;

    @TableField("phid")
    private Long phid;

    @TableField("fromtype")
    private String fromtype;

//    @TableField("user_yyid")
//    private String userYyid;

//    @TableField("user_yyid2")
//    private String userYyid2;
}
