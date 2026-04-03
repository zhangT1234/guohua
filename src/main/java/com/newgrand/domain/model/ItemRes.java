package com.newgrand.domain.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("res_bs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRes {

    @TableField("phid")
    private Long phid;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("ffid")
    private String ffid;

    @TableField("resource_type")
    private String resourceType;

    @TableField("user_ofsid")
    private String userOfsid;

}
