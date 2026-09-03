package com.newgrand.service.impl;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.newgrand.domain.model.UserWechat;
import com.newgrand.domain.model.WechatLoginResult;
import com.newgrand.mapper.UserWechatMapper;
import com.newgrand.service.UserWechatService;
import com.newgrand.utils.CacheInfo;
import com.newgrand.utils.CacheUtil;
import com.newgrand.utils.HttpUtils;
import com.newgrand.utils.NGEncodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户 openid 绑定服务实现
 */
@Slf4j
@Service
public class UserWechatServiceImpl implements UserWechatService {

    private static final int QR_EXPIRE_SECONDS = 600;
    private static final int SCAN_BIND_EXPIRE_SECONDS = 60;
    private static final String QR_SCENE_CACHE_PREFIX = "wechat:qr:scene:";
    /** 扫码后 sceneStr -> openid 缓存前缀 */
    public static final String QR_SCAN_CACHE_PREFIX = "wechat:qr:scan:";
    private static final String QRSCENE_PREFIX = "qrscene_";
    private static final int SCENE_GENERATE_MAX_RETRY = 5;
    private static final int COOKIE_MAX_AGE = 604800;

    @Value("${wechat.token:yuanzang}")
    private String wechatToken;

    /** 登录相关接口根地址，如 https://gdyz.gd.gov.cn */
    @Value("${i8.loginUrl:}")
    private String loginUrl;

    @Value("${i8.database}")
    private String i8Database;

    @Value("${i8.esn:760833}")
    private String i8Esn;

    /** H5 kernelsession 固定 Authorization */
    private static final String KERNEL_SESSION_AUTH =
            "ngTokenKey$NEWGRAND-WEB$1787918668$761f13a7-53b9-47e1-88c1-67bfa4472040$ja";

    @Autowired
    private UserWechatMapper userWechatMapper;

    @Autowired
    private WorkFlowService workFlowService;

    @Override
    public UserWechat getByUsername(String username) {
        return userWechatMapper.selectOne(
                new QueryWrapper<UserWechat>().eq("username", username));
    }

    @Override
    public UserWechat getByOpenid(String openid) {
        return userWechatMapper.selectOne(
                new QueryWrapper<UserWechat>().eq("openid", openid));
    }

    @Override
    public UserWechat getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        String openid = workFlowService.getOpenid(code);
        if (StringUtils.isEmpty(openid)) {
            return null;
        }
        UserWechat userWechat = getByOpenid(openid);
        // 未绑定也返回 openid，便于前端继续走绑定流程
        if (userWechat == null) {
            return UserWechat.builder().openid(openid).build();
        }
        return userWechat;
    }

    @Override
    public Map<String, String> createTempQrCode() {
        String sceneStr = generateUniqueSceneStr();
        if (StringUtils.isEmpty(sceneStr)) {
            log.error("生成唯一 scene_str 失败");
            return null;
        }

        JSONObject scene = new JSONObject();
        scene.put("scene_str", sceneStr);
        JSONObject actionInfo = new JSONObject();
        actionInfo.put("scene", scene);
        JSONObject body = new JSONObject();
        body.put("expire_seconds", QR_EXPIRE_SECONDS);
        body.put("action_name", "QR_STR_SCENE");
        body.put("action_info", actionInfo);

        try {
            String ticket = createQrTicket(body, true);
            if (StringUtils.isEmpty(ticket)) {
                CacheUtil.remove(QR_SCENE_CACHE_PREFIX + sceneStr);
                return null;
            }

            CacheInfo cacheInfo = CacheUtil.get(QR_SCENE_CACHE_PREFIX + sceneStr);
            if (cacheInfo != null) {
                cacheInfo.setInfo(ticket);
                CacheUtil.put(QR_SCENE_CACHE_PREFIX + sceneStr, cacheInfo);
            }

            String qrUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="
                    + URLEncoder.encode(ticket, StandardCharsets.UTF_8.name());
            Map<String, String> result = new HashMap<>();
            result.put("qrUrl", qrUrl);
            result.put("sceneStr", sceneStr);
            return result;
        } catch (Exception e) {
            CacheUtil.remove(QR_SCENE_CACHE_PREFIX + sceneStr);
            log.error("创建微信临时二维码失败", e);
            return null;
        }
    }

    /**
     * 生成32位唯一随机 scene_str，并预占本地缓存（有效期600秒）
     */
    private String generateUniqueSceneStr() {
        for (int i = 0; i < SCENE_GENERATE_MAX_RETRY; i++) {
            String sceneStr = UUID.randomUUID().toString().replace("-", "");
            CacheInfo cacheInfo = new CacheInfo();
            cacheInfo.setInfo("");
            cacheInfo.setValidTime(System.currentTimeMillis() + QR_EXPIRE_SECONDS * 1000L);
            if (CacheUtil.putIfAbsent(QR_SCENE_CACHE_PREFIX + sceneStr, cacheInfo)) {
                return sceneStr;
            }
        }
        return null;
    }

    @Override
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce)) {
            return false;
        }
        String[] arr = new String[]{wechatToken, timestamp, nonce};
        Arrays.sort(arr);
        String sha1 = SecureUtil.sha1(arr[0] + arr[1] + arr[2]);
        return signature.equals(sha1);
    }

    @Override
    public void handleEventPush(String xml) {
        if (StringUtils.isEmpty(xml)) {
            return;
        }
        Map<String, Object> msg;
        try {
            msg = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            log.error("解析微信事件推送 XML 失败", e);
            return;
        }
        if (msg == null) {
            return;
        }
        String msgType = stringVal(msg.get("MsgType"));
        if (!"event".equalsIgnoreCase(msgType)) {
            return;
        }
        String event = stringVal(msg.get("Event"));
        String eventKey = stringVal(msg.get("EventKey"));
        String openid = stringVal(msg.get("FromUserName"));
        String sceneStr = resolveSceneStr(event, eventKey);
        if (StringUtils.isEmpty(sceneStr) || StringUtils.isEmpty(openid)) {
            return;
        }
        // 仅处理本系统生成的二维码场景值
        if (!CacheUtil.hasKey(QR_SCENE_CACHE_PREFIX + sceneStr)) {
            log.info("扫码场景值不在本地缓存中, sceneStr={}", sceneStr);
            return;
        }
        CacheInfo scanCache = new CacheInfo();
        scanCache.setInfo(openid);
        scanCache.setValidTime(System.currentTimeMillis() + SCAN_BIND_EXPIRE_SECONDS * 1000L);
        CacheUtil.put(QR_SCAN_CACHE_PREFIX + sceneStr, scanCache);
        log.info("扫码绑定缓存成功, sceneStr={}, openid={}, expire={}s",
                sceneStr, openid, SCAN_BIND_EXPIRE_SECONDS);
    }

    /**
     * SCAN: EventKey 即为 scene_str；
     * 未关注扫码关注: EventKey 为 qrscene_ + scene_str
     */
    private String resolveSceneStr(String event, String eventKey) {
        if (StringUtils.isEmpty(event) || StringUtils.isEmpty(eventKey)) {
            return null;
        }
        if ("SCAN".equalsIgnoreCase(event)) {
            return eventKey;
        }
        if ("subscribe".equalsIgnoreCase(event) && eventKey.startsWith(QRSCENE_PREFIX)) {
            return eventKey.substring(QRSCENE_PREFIX.length());
        }
        return null;
    }

    private String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public WechatLoginResult loginBySceneStr(String sceneStr) {
        if (StringUtils.isEmpty(sceneStr)) {
            return null;
        }
        CacheInfo scanCache = CacheUtil.get(QR_SCAN_CACHE_PREFIX + sceneStr);
        if (scanCache == null || StringUtils.isEmpty(scanCache.getInfo())) {
            log.info("扫码缓存不存在或已过期, sceneStr={}", sceneStr);
            return null;
        }
        String openid = scanCache.getInfo();
        UserWechat userWechat = getByOpenid(openid);
        if (userWechat == null || StringUtils.isEmpty(userWechat.getUsername())) {
            log.info("未找到 openid 绑定用户, openid={}", openid);
            return null;
        }
        String loginId = userWechat.getUsername();
        try {
            // loginid / ucode 均为 DES(ECB/PKCS7) 加密后的 hex，密钥 d(3D0;Ia
            String encryptedLoginId = NGEncodeUtil.desEncryptToHexString(loginId);
            String encryptedUcode = NGEncodeUtil.desEncryptToHexString(i8Database);

            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("esn", i8Esn));
            form.add(new BasicNameValuePair("loginid", encryptedLoginId));
            form.add(new BasicNameValuePair("ucode", encryptedUcode));
            form.add(new BasicNameValuePair("isapp", "0"));

            return postKernelSession(form);
        } catch (Exception e) {
            log.error("微信扫码登录失败, sceneStr={}, loginId={}", sceneStr, loginId, e);
            return null;
        }
    }

    @Override
    public boolean mockSceneCache(String sceneStr, String openid) {
        if (StringUtils.isEmpty(sceneStr) || StringUtils.isEmpty(openid)) {
            return false;
        }
        CacheInfo sceneCache = new CacheInfo();
        sceneCache.setInfo("mock");
        sceneCache.setValidTime(System.currentTimeMillis() + QR_EXPIRE_SECONDS * 1000L);
        CacheUtil.put(QR_SCENE_CACHE_PREFIX + sceneStr, sceneCache);

        CacheInfo scanCache = new CacheInfo();
        scanCache.setInfo(openid);
        scanCache.setValidTime(System.currentTimeMillis() + SCAN_BIND_EXPIRE_SECONDS * 1000L);
        CacheUtil.put(QR_SCAN_CACHE_PREFIX + sceneStr, scanCache);
        log.info("测试写入扫码缓存成功, sceneStr={}, openid={}", sceneStr, openid);
        return true;
    }

    private WechatLoginResult postKernelSession(List<NameValuePair> form) throws Exception {
        String base = trimSlash(loginUrl);
        String url = base + "/rest/api/kernelsession";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Authorization", KERNEL_SESSION_AUTH);
            httpPost.setHeader("Origin", base);
            httpPost.setHeader("Referer", base + "/");
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONObject loginData = JSONObject.parseObject(body);
                if (loginData == null) {
                    log.error("kernelsession 返回为空, body={}", body);
                    return null;
                }
                if (!isLoginSuccess(loginData)) {
                    log.error("kernelsession 登录失败: {}", body);
                    return WechatLoginResult.builder().loginData(loginData).build();
                }
                List<WechatLoginResult.CookieItem> cookies = parseSetCookies(response.getHeaders("Set-Cookie"));
                return WechatLoginResult.builder()
                        .loginData(loginData)
                        .cookies(cookies)
                        .build();
            }
        }
    }

    private boolean isLoginSuccess(JSONObject loginData) {
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

    private List<WechatLoginResult.CookieItem> parseSetCookies(Header[] setCookieHeaders) {
        List<WechatLoginResult.CookieItem> cookies = new ArrayList<>();
        if (setCookieHeaders == null) {
            return cookies;
        }
        for (Header header : setCookieHeaders) {
            String raw = header.getValue();
            if (StringUtils.isEmpty(raw)) {
                continue;
            }
            String[] parts = raw.split(";");
            if (parts.length == 0 || !parts[0].contains("=")) {
                continue;
            }
            String[] nv = parts[0].split("=", 2);
            String name = nv[0].trim();
            String value = nv.length > 1 ? nv[1].trim() : "";
            Integer maxAge = COOKIE_MAX_AGE;
            for (int i = 1; i < parts.length; i++) {
                String attr = parts[i].trim();
                if (attr.toLowerCase().startsWith("max-age=")) {
                    try {
                        maxAge = Integer.parseInt(attr.substring(8).trim());
                    } catch (NumberFormatException ignored) {
                        // keep default
                    }
                }
            }
            cookies.add(WechatLoginResult.CookieItem.builder()
                    .name(name)
                    .value(value)
                    .maxAge(maxAge)
                    .build());
        }
        return cookies;
    }

    private String trimSlash(String url) {
        if (url == null) {
            return "";
        }
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String createQrTicket(JSONObject body, boolean retryOnTokenInvalid) throws Exception {
        String accessToken = workFlowService.getToken();
        if (StringUtils.isEmpty(accessToken)) {
            log.error("微信创建二维码失败: access_token 为空，请检查公众号 appid/secret 及 IP 白名单");
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        querys.put("access_token", accessToken);
        HttpResponse response = HttpUtils.doPost(WorkFlowService.WECHAT_HOST, "/cgi-bin/qrcode/create",
                "POST", headers, querys, body.toJSONString());
        String resultStr = EntityUtils.toString(response.getEntity(), "utf-8");
        JSONObject result = JSONObject.parseObject(resultStr);
        if (result == null) {
            return null;
        }
        if (result.containsKey("ticket")) {
            return result.getString("ticket");
        }
        int errcode = result.getIntValue("errcode");
        log.error("微信创建二维码失败: {}", resultStr);
        if (retryOnTokenInvalid && (errcode == 40001 || errcode == 42001)) {
            CacheUtil.remove(WorkFlowService.ACCESS_TOKEN_KEY);
            return createQrTicket(body, false);
        }
        return null;
    }

    @Override
    public UserWechat saveByOpenid(String openid, String username) {
        if (StringUtils.isEmpty(openid) || StringUtils.isEmpty(username)) {
            return null;
        }
        // 本地已有 openid 或 username 对应关系时先删除
        userWechatMapper.delete(new QueryWrapper<UserWechat>()
                .eq("openid", openid)
                .or()
                .eq("username", username));

        Date now = new Date();
        UserWechat entity = UserWechat.builder()
                .username(username)
                .openid(openid)
                .insertDt(now)
                .updateDt(now)
                .build();
        if (userWechatMapper.insert(entity) > 0) {
            return entity;
        }
        return null;
    }

    @Override
    public boolean bind(String username, String openid) {
        Date now = new Date();
        UserWechat exist = getByUsername(username);
        if (exist == null) {
            UserWechat entity = UserWechat.builder()
                    .username(username)
                    .openid(openid)
                    .insertDt(now)
                    .updateDt(now)
                    .build();
            return userWechatMapper.insert(entity) > 0;
        }
        exist.setOpenid(openid);
        exist.setUpdateDt(now);
        return userWechatMapper.updateById(exist) > 0;
    }

    @Override
    public boolean save(UserWechat userWechat) {
        if (userWechat.getInsertDt() == null) {
            userWechat.setInsertDt(new Date());
        }
        if (userWechat.getUpdateDt() == null) {
            userWechat.setUpdateDt(new Date());
        }
        return userWechatMapper.insert(userWechat) > 0;
    }

    @Override
    public boolean update(UserWechat userWechat) {
        userWechat.setUpdateDt(new Date());
        return userWechatMapper.updateById(userWechat) > 0;
    }
}
