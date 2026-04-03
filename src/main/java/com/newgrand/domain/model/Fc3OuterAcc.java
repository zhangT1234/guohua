package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("fc3_outer_acc")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fc3OuterAcc {

    @TableField("phid")
    private Long phid;

    @TableField("acc_code")
    private String accCode;

    @TableField("acc_name")
    private String accName;

    @TableField("phid_bank")
    private String phidBank;

    @TableField("accountno")
    private String accountno;

    @TableField("accountname")
    private String accountname;

    @TableField("acc_type")
    private String accType;

    @TableField("incom_type")
    private String incomType;

    @TableField("phid_open_org")
    private String phidOpenOrg;

    @TableField("phid_org")
    private String phidOrg;

    @TableField("is_stop")
    private String isStop;

    @TableField("user_ofsid")
    private String userOfsid;

}
