package com.newgrand.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/20 17:41
 * @Description: TODO
 */
@Data
@TableName("sec_dev_task_msg")
@Builder
public class SecDevTaskMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("phid")
    private String phid;

    @TableField("taskid")
    private String taskid;

    @TableField("userid")
    private String userid;

    @TableField("status")
    private String status;

    @TableField("url")
    private String url;

    @TableField("dbname")
    private String dbname;

    @TableField("pushadd")
    private String pushadd;

    @TableField("pushaddtime")
    private String pushaddtime;

    @TableField("pushdone")
    private String pushdone;

    @TableField("pushdonetime")
    private String pushdonetime;

    @TableField("pushdel")
    private String pushdel;

    @TableField("pushdeltime")
    private String pushdeltime;
}
