package com.newgrand.domain.dbo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author zbs
 * @date 2022-11-20 12:20:13
 */
@Data
@TableName("HR_EPM_MAIN")
public class HrEpmMainEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long phid;

    private String ccode;

    private String cno;

    private String cname;

    private String sexno;

    private Long cboo;

    private Long dept;

    private Long emptype;

    private String cardno;

    private Long cardtype;

    private String cdt;

}
