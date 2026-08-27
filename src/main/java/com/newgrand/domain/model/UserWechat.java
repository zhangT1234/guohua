package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户 openid 绑定表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_wechat")
public class UserWechat implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("openid")
    private String openid;

    @TableField("insert_dt")
    private Date insertDt;

    @TableField("update_dt")
    private Date updateDt;
}
