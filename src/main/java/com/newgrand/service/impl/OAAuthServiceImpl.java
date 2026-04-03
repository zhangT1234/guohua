package com.newgrand.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.newgrand.config.OAProperties;
import com.newgrand.service.OAAuthService;
import com.newgrand.utils.CacheInfo;
import com.newgrand.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OA 认证服务实现
 *
 * @author guohua
 */
@Slf4j
@Service
public class OAAuthServiceImpl implements OAAuthService {

    @Resource
    private OAProperties oaProperties;

    /**
     * 缓存Key常量
     */
    private static final String CACHE_KEY_SEC = "oa:ecology:secrit";
    private static final String CACHE_KEY_SPK = "oa:ecology:spk";
    private static final String CACHE_KEY_TOKEN = "oa:ecology:token";

    @Override
    public void register() {
        String url = oaProperties.getBaseUrl() + "/api/ec/dev/auth/regist";

        try {
            log.info("[OA] 开始注册，AppId: {}", oaProperties.getAppid());

            HttpResponse response = HttpRequest.post(url)
                    .header("appid", oaProperties.getAppid())
                    .timeout(5000)
                    .execute();

            String responseBody = response.body();
            log.info("[OA] 注册响应: {}", responseBody);

            // 解析响应，获取 secrit 和 spk
            JSONObject jsonObject = JSONUtil.parseObj(responseBody);
            String secrit = jsonObject.getStr("secrit");
            String spk = jsonObject.getStr("spk");

            if (StrUtil.isEmpty(secrit) || StrUtil.isEmpty(spk)) {
                throw new Exception("注册失败：secrit 或 spk 为空");
            }

            // 缓存 secrit 和 spk
            CacheInfo secritInfo = new CacheInfo();
            secritInfo.setInfo(secrit);
            secritInfo.setValidTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
            CacheUtil.put(CACHE_KEY_SEC, secritInfo);
            CacheInfo spkInfo = new CacheInfo();
            spkInfo.setInfo(spk);
            spkInfo.setValidTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
            CacheUtil.put(CACHE_KEY_SPK, spkInfo);

            log.info("[OA] 注册成功，secrit长度: {}, spk长度: {}", secrit.length(), spk.length());

        } catch (Exception e) {
            log.error("[OA] 注册失败", e);
        }
    }

    @Override
    public String getToken(String userId) {
        // 1. 尝试从缓存获取 token
        String token = null;
        CacheInfo cacheInfo = CacheUtil.get(CACHE_KEY_TOKEN + "-" + userId);
        if (cacheInfo != null) {
            token = cacheInfo.getInfo();
        }
        if (StrUtil.isNotEmpty(token)) {
            // 获取到的过期时间戳
            Long validTime = cacheInfo.getValidTime();
            long now = System.currentTimeMillis();
            if (validTime < now) {
                log.info("[OA] 从缓存获取 Token 过期");
                // 过期重新获取
                return refreshToken(userId);
            }
            log.info("[OA] 从缓存获取 Token 成功");
            return token;
        }

        // 2. 缓存不存在，重新获取
        return refreshToken(userId);
    }

    /**
     * 刷新 Token
     */
    private String refreshToken(String userId) {
        // 1. 从缓存获取 secrit 和 spk
//        String secrit = stringRedisTemplate.opsForValue().get(CACHE_KEY_SEC);
//        String spk = stringRedisTemplate.opsForValue().get(CACHE_KEY_SPK);
//
//        // 2. 如果缓存不存在，先注册
//        if (StrUtil.isEmpty(secrit) || StrUtil.isEmpty(spk)) {
//            log.info("[OA] 缓存中无 secrit 或 spk，开始注册");
//            register();
//            secrit = stringRedisTemplate.opsForValue().get(CACHE_KEY_SEC);
//            spk = stringRedisTemplate.opsForValue().get(CACHE_KEY_SPK);
//        }

        String spk = oaProperties.getSpk();
        String secret = oaProperties.getSecret();
        if (StrUtil.isEmpty(spk) || StrUtil.isEmpty(secret)) {
            Map<String, String> spkSecret = getSpkSecret();
            spk = spkSecret.getOrDefault("spk", "");
            secret = spkSecret.getOrDefault("secret", "");
        }

        // 3. 使用 spk 对 secrit 进行 RSA 加密
        RSA rsa = new RSA(null, spk);
        String encryptedSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);

        log.info("[OA] 使用 spk 加密 secrit，加密后长度: {}", encryptedSecret.length());

        // 4. 调用获取 Token 接口
        String url = oaProperties.getBaseUrl() + "/api/ec/dev/auth/applytoken";
        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("appid", oaProperties.getAppid())
                    .header("secret", encryptedSecret)
                    .header("time", "3600")
                    .timeout(5000)
                    .execute();

            String responseBody = response.body();
            log.info("[OA] 获取 Token 响应: {}", responseBody);

            // 解析响应，获取 token
            JSONObject jsonObject = JSONUtil.parseObj(responseBody);
            String token = jsonObject.getStr("token");

            if (StrUtil.isEmpty(token)) {
                throw new Exception("获取 Token 失败：" + jsonObject.getStr("msg"));
            }

            // 5. 缓存 token (1小时)
            CacheInfo tokenInfo = new CacheInfo();
            tokenInfo.setInfo(token);
            tokenInfo.setValidTime(System.currentTimeMillis() + (1 * 60 * 60 * 1000));
            CacheUtil.put(CACHE_KEY_TOKEN + "-" + userId, tokenInfo);

            log.info("[OA] 获取 Token 成功");
            return token;

        } catch (Exception e) {
            log.error("[OA] 获取 Token 失败", e);
            return null;
        }
    }

    @Override
    public String encryptUserId(String userId) {
        // 1. 获取 spk
//        String spk = stringRedisTemplate.opsForValue().get(CACHE_KEY_SPK);

//        // 2. 如果不存在，先注册
//        if (StrUtil.isEmpty(spk)) {
//            register();
//            spk = stringRedisTemplate.opsForValue().get(CACHE_KEY_SPK);
//        }

        String spk = oaProperties.getSpk();

        // 3. 使用 spk 对 userId 进行 RSA 加密
        RSA rsa = new RSA(null, spk);
        String encryptedUserId = rsa.encryptBase64(userId, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);

        log.info("[OA] 加密 userId 成功，原始: {}, 加密后长度: {}", userId, encryptedUserId.length());

        return encryptedUserId;
    }

    @Override
    public String getSSOToken(String loginId) {
        String url = oaProperties.getBaseUrl() + "/ssologin/getToken";

        try {
            log.info("[OA] 开始获取 SSO Token，loginId: {}", loginId);

            // 调用 SSO Token 接口（application/x-www-form-urlencoded）
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .form("appid", oaProperties.getSsoAppid())
                    .form("loginid", loginId)
                    .timeout(oaProperties.getConnectTimeout())
                    .execute();

            // 响应体是纯文本的 token 值
            String token = response.body();
            log.info("[OA] 获取 SSO Token 响应长度: {}", token != null ? token.length() : 0);

            if (StrUtil.isEmpty(token)) {
                throw new Exception("获取 SSO Token 失败：返回值为空");
            }

            log.info("[OA] 获取 SSO Token 成功，loginId: {}, token长度: {}", loginId, token.length());
            return token;

        } catch (Exception e) {
            log.error("[OA] 获取 SSO Token 失败，loginId: {}", loginId, e);
            return  null;
        }
    }

    @Override
    public Map<String, String> getSpkSecret() {
        HashMap<String, String> result = new HashMap<>();
        String url = oaProperties.getBaseUrl() + "/api/ec/dev/auth/regist";
        try {
            log.info("[OA] 开始注册，AppId: {}", oaProperties.getAppid());
            HttpResponse response = HttpRequest.post(url)
                    .header("appid", oaProperties.getAppid())
                    .timeout(5000)
                    .execute();
            String responseBody = response.body();
            log.info("[OA] 注册响应: {}", responseBody);
            // 解析响应，获取 secrit 和 spk
            JSONObject jsonObject = JSONUtil.parseObj(responseBody);
            String secrit = jsonObject.getStr("secrit");
            String spk = jsonObject.getStr("spk");
            if (StrUtil.isEmpty(secrit) || StrUtil.isEmpty(spk)) {
                throw new Exception("注册失败：secrit 或 spk 为空");
            }

            log.info("[OA] 注册成功，secrit长度: {}, spk长度: {}", secrit.length(), spk.length());
            result.put("spk", spk);
            result.put("secret", secrit);
            return result;
        }  catch (Exception e) {
            log.error("[OA] 注册失败", e);
            return result;
        }
    }
}


