package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhanglixin
 * @Data: 2022/8/26 14:49
 * @Description: 日志实体类
 */

@Data
@TableName("p_form_seconddev_log_m")
@Builder
public class PFormSeconddevlogM implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("bill_no")
    private String bill_no;

    @TableField("is_wf")
    private String is_wf;

    @TableField("asr_flg")
    private String asr_flg;

    @TableField("bill_dt")
    private Date bill_dt;

    @TableField("fillpsn")
    private String fillpsn;

    @TableField("ischeck")
    private String ischeck;

    @TableField("checkpsn")
    private String checkpsn;

    @TableField("check_dt")
    private Date check_dt;

    @TableField("pc")
    private String pc;

    @TableField("ocode")
    private String ocode;

    @TableField("title")
    private String title;

    @TableField("da_flg")
    private String da_flg;

    @TableField("code")
    private String code;

    @TableField("creator")
    private String creator;

    @TableField("editor")
    private String editor;

    @TableField("ng_insert_dt")
    private Date ng_insert_dt;

    @TableField("ng_update_dt")
    private Date ng_update_dt;

    @TableField("ng_record_ver")
    private String ng_record_ver;

    @TableField("cur_orgid")
    private String cur_orgid;

    @TableField("ng_phid_org")
    private String ng_phid_org;

    @TableField("ng_phid_cu")
    private String ng_phid_cu;

    @TableField("ng_phid_bp")
    private String ng_phid_bp;

    @TableField("ng_phid_original")
    private String ng_phid_original;

    @TableField("ng_orgid_original")
    private String ng_orgid_original;

    @TableField("ng_phid_ui_scheme")
    private String ng_phid_ui_scheme;

    @TableField("ng_sv_search_key")
    private String ng_sv_search_key;

    @TableField("ng_sd_search_key")
    private String ng_sd_search_key;

    @TableField("ng_share_sign")
    private String ng_share_sign;

    @TableField("phid_schemeid")
    private String phid_schemeid;

    @TableField("imp_info")
    private String imp_info;

    @TableField("phid_cycle")
    private String phid_cycle;

    @TableField("printcount")
    private String printcount;

    @TableField("u_level")
    private String u_level;

    @TableField("u_tabname")
    private String u_tabname;

    @TableField("u_jkms")
    private String u_jkms;

    @TableField("u_log1")
    private String u_log1;

    @TableField("u_log2")
    private String u_log2;

    @TableField("u_log3")
    private String u_log3;

    @TableField("u_log4")
    private String u_log4;

}
