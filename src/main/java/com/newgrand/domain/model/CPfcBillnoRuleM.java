package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 16:13
 * @Description: TODO
 */
@Data
@TableName("c_pfc_billnorule_m")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CPfcBillnoRuleM implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("c_btype")
    private String cBtype;

    @TableField("c_code")
    private String cCode;

    @TableField("codemode")
    private String codemode;

    @TableField("user_mod_flg")
    private String userModFlg;

    @TableField("sysflg")
    private String sysflg;
}
