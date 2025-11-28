package com.newgrand.domain.model;

import lombok.Data;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 16:34
 */
@Data
public class I8ReturnModel {
    /**
     * 编码
     */
    private String code = "0";
    /**
     * 成功
     */
    private Boolean IsOk = true;
    /**
     * 成功标识
     */
    private String ErrorCode = "S";
    /**
     * 失败||成功返回消息
     */
    private String Message;
    /**
     * 返回体
     */
    private Object data;
    private String phid;
}
