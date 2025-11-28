package com.newgrand.domain.model;

import lombok.Data;

@Data
public class BipReturnModel {
    /**
     * 返回码，调用成功时返回200
     */
    private String code = "200";

    /**
     * 调用失败时的错误信息
     */
    private String message;

}
