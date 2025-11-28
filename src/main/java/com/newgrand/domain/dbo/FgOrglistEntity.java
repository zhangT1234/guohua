package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author zbs
 * @date 2022-11-18 17:36:49
 */
@Data
@TableName("FG_ORGLIST")
public class FgOrglistEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long phid;

    private Long parent_orgid;

    private String ocode;

    private String oname;

    private String bopomofo;

    private String codevalue;

    private String orgindex;

    private String ifcorp;

    private String iflogin;

    private String isactive;

    private String orgtype;

}
