package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author zbs
 * @date 2022-11-20 11:14:41
 */
@Data
@TableName("FG_FCUR")
public class FgFcurEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fc_code;

    private String fc_name;

    private String fc_sign;

    private String er_mode;

    private Long fc_er;

    private Long iscurfc;

    private String verdtm;

    @TableId
    private Long phid;

    private Long cur_orgid;

    private Long creator;

    private Long editor;

    private String ng_insert_dt;

    private String ng_update_dt;

    private Long ng_record_ver;

    private Long sysflg;

    private Long ng_phid_cu;

    private Long ng_phid_bp;

    private Long ng_phid_org;

    private Long ng_phid_ui_scheme;

    private Long ng_phid_original;

    private Long ng_orgid_original;

    private String ng_sv_search_key;

    private String ng_sd_search_key;

    private Long ng_share_sign;

    private String ng_bp_phids;

    private String ng_bp_names;

    private String bill_no;

    private String bill_name;


}
