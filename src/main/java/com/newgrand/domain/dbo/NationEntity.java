package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author zbs
 * @date 2022-11-18 21:13:03
 */
@Data
@TableName("NATION")
public class NationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long phid;

    private String nationno;

    private String nat_name;

    private String nat_name_e;

    private String domainame;

    private String tele_no;

    private String verdtm;

    private Long creator;

    private Long editor;

    private String ng_insert_dt;

    private String ng_update_dt;

    private Long ng_record_ver;

    private Long cur_orgid;

    private Long ng_phid_org;

    private Long ng_phid_cu;

    private Long ng_phid_bp;

    private Long ng_phid_original;

    private Long ng_orgid_original;

    private Long ng_phid_ui_scheme;

    private String ng_sv_search_key;

    private String ng_sd_search_key;

    private Long ng_share_sign;


}
