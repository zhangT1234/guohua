package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/5 10:25
 * @Description: 组织关系树
 */
@Data
@TableName("fg_orgrelatitem")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgOrgrelatitem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("phid")
    private String phid;

    @TableField("relatid")
    private String relatid;

    @TableField("ocode")
    private String ocode;

    @TableField("parentorg")
    private String parentorg;

    @TableField("relatindex")
    private String relatindex;

    @TableField("relid")
    private String relid;

    @TableField("ordertype")
    private String ordertype;

    @TableField("org_id")
    private String orgId;

    @TableField("parent_orgid")
    private String parentOrgid;

    @TableField("attrcode")
    private String attrcode;

    @TableField("system_orgtype")
    private String systemOrgtype;


}
