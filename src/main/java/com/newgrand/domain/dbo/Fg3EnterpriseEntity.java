package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zbs
 * @date 2022-11-18 21:21:12
 */
@Data
@TableName("FG3_ENTERPRISE")
public class Fg3EnterpriseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long phid;

    private String fromtype;

    private Long fromorg_id;

    private String compno;

    private String compname;

    private String simpname;

    private String simpname2;

    private Long person_flg;

    private String address;

    private String url;

    private String email;

    private Long scale_id;

    private Long enternature_id;

    private Long tradetype_id;

    private Long tradegrade_id;

    private Long parentcomp_id;

    private Long istemp;

    private String establishdate;

    private String unisocialcredit;

    private String taxno;

    private String unitcode;

    private String shopno;

    private Long taxpayertype;

    private String taxpayername;

    private Long taxbank_id;

    private String taxaccountno;

    private String taxaddress;

    private String taxtelephone;

    private Long regmoney;

    private String regdt;

    private String regscope;

    private String person;

    private String cardtype;

    private String cardno;

    private Long auditflg;

    private Long auditpsn_id;

    private String auditdt;


}
