package com.newgrand.domain.model;

import lombok.Data;

import java.util.List;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/6/16 17:23
 */
@Data
public class WorkFlowModel {

    /**
     * 数据库账套
     */
    private String dbName;
    /**
     * 工作流状态
     */
    private String status;
    /**
     * 任务id
     */
    private String taskid;
    /**
     * 任务描述
     */
    private String title;
    /**
     * 工作流连接
     */
    private String url;
    /**
     * 当前用户账号
     */
    private String currentUser;
    /**
     * 待办人员
     */
    private List<String> users;
}
