package com.newgrand.domain.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信扫码登录结果（含需回写给前端的 Cookie）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatLoginResult {

    /** kernelSessionNew 返回的 JSON */
    private JSONObject loginData;

    /** 需设置到浏览器的 cookie：name=value */
    @Builder.Default
    private List<CookieItem> cookies = new ArrayList<>();

    public boolean isSuccess() {
        if (loginData == null) {
            return false;
        }
        if ("Success".equalsIgnoreCase(loginData.getString("status"))) {
            return true;
        }
        if (loginData.getBooleanValue("Success")) {
            return true;
        }
        return "success".equalsIgnoreCase(loginData.getString("Status"));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookieItem {
        private String name;
        private String value;
        private Integer maxAge;
    }
}
