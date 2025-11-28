package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/19 20:27
 * @Description: TODO
 */
@Data
@TableName("act_hi_taskinst")
@Builder
public class ActHiTaskinst implements Serializable {

    @TableId
    @TableField("id_")
    private String id_;

    @TableField("proc_inst_id_")
    private String proc_inst_id_;

    @TableField("delete_reason_")
    private String delete_reason_;

    @TableField("owner_")
    private String owner_;

    @TableField("assignee_")
    private String assignee_;

}
