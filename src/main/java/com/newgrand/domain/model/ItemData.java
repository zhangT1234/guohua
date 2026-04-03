package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("itemdata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemData {

    @TableField("phid")
    private Long phid;

    @TableField("itemno")
    private String itemNo;

    @TableField("itemname")
    private String itemName;

    @TableField("phid_resbs")
    private Long phidResbs;

    @TableField("ffid")
    private String ffid;

    @TableField("resource_type")
    private String resourceType;

    @TableField("user_ofsid")
    private String userOfsid;

}
