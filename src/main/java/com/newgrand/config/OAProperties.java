package com.newgrand.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OA 集成配置类
 *
 * @author guohua
 */
@Data
@Component
@ConfigurationProperties(prefix = "oa.ecology")
public class OAProperties {

    /**
     * 是否启用 OA 集成
     */
    private Boolean enabled = true;

    /**
     * OA 系统地址（包含协议和端口，但不包含最后的斜杠）
     * 例如：http://10.10.10.151:8888
     */
    //private String baseUrl = "http://10.10.10.151:8888";    //测试
    private String baseUrl = "http://www.greatagroup.com:8888"; //正式

    /**
     * ecology 系统发放的授权许可证 (appid)
     */
    // private String appid = "4F200BB6-10A2-47D7-9F3C-C85F9517B0EB"; //测试
     private String appid = "85988438-64cc-4eb2-a83f-cef69c0c8349";   //正式

    /**
     * 默认用户 ID（用于 OA 认证）
     */
    private String userId = "1";

    /**
     * Token 缓存时长（秒），默认 3600 秒（1小时）
     */
    private Integer tokenCacheDuration = 3600;

    /**
     * 连接超时时间（毫秒），默认 5000ms
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒），默认 10000ms
     */
    private Integer readTimeout = 10000;

    /**
     * KPI 考核审批工作流 ID
     */
    private String kpiWorkflowId = "549";

    /**
     * 录用审批工作流 ID
     */
    private String offerWorkflowId = "";

    /**
     * 试用审批工作流 ID
     */
    private String probationWorkflowId = "";

    /**
     * 转正审批工作流 ID
     */
    private String regularWorkflowId = "";

    /**
     * 离职审批工作流 ID
     */
    private String leaveWorkflowId = "";

    /**
     * SSO 登录 Appid
     */
    private String ssoAppid = "81460d3b-0b3f-45b5-8cf4-6c0e7967cb8c";

    //正式
    private String spk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9Yobl8fMX5pRe+7jUEF6+n02PbgKj9hvW2adIEVxxT+CCYN5KGMj9HGOjO4A2U+5p2z/dmzZ5r6O9rdyBpYlH8tBjoryHrqaS/dC6stm2kn+paVPZb/lJ0/phsWov7idceLqvuLhrwD9awUAZ6zyzNXo62xllZ5igpsIZcPLySgVsN37Hjcg++HJJUmjDmwFo9grrK2E1+fifVuVgljqoBmNQpjSBHZftLGWrQWndoDXEgz6u8ARL+bu8VLBI8ICuPa41dsa4zbiBnWeykozSxHu67VKhazqby8w8ooUJgPbnMfZmegKOaUVdJpV1DN9e2aNx8ksA2NIXmtrmfgO1QIDAQAB";
    //正式
    private String secret = "f3cbaeec-7e49-4788-9fab-95f00903393f";

    //测试
   // private String secret = "cf45a304-aed4-4b3d-9762-240a10c457ce";

    //测试
   // private String spk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGhBbfUqqld2ZLgCmU1TUx51k8tySQqMGG9XRXrCazY2MBZ+xEnThMzI1bObwjFOdBJTn8Moj2bgM7ByOHY4U1w5WfrMkxtGyIZMBk136b87S2iJe2h6ekyq0cBOq+dxWyvwsBI6G+VGV17cSNiDMGi5ZiG/EUr1CqdcQ4fy139QIDAQAB";

}

