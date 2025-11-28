package com.newgrand.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BipTodoDTO {
    @ApiModelProperty("消息ID，用于幂等校验，同一消息ID只接收一次。格式建议“业务标识:唯一编码")
    private String srcMsgId;

    @ApiModelProperty("友互通userId列表")
    private List<String> yyUserIds;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("通知内容")
    private String content;

    @ApiModelProperty("移动端打开地址")
    private String mUrl;

    @ApiModelProperty("Web端打开地址")
    private String webUrl;

    @ApiModelProperty("应用ID")
    private String appId;

    @ApiModelProperty("待办事件唯一KEY，唯一且不超过200字符")
    private String businessKey;

    @ApiModelProperty("前审批人列表")
    private List<String> currentApprovers;
}
