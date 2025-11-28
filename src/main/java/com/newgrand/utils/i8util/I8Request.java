package com.newgrand.utils.i8util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.impl.UipLog;
import com.newgrand.utils.JSONValid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 模拟 i8 前端参数，封装并请求推送数据到后端接口
 */
@Slf4j
@Service
public class I8Request {//继承InitializingBean ，解决构造函数中使用注入对象报空指针的问题

    @Autowired
    private UipLog uipLog;

    @Value("${i8.url}")
    private String i8url;
    @Value("${i8.user}")
    private String i8user;
    @Value("${i8.pwd}")
    private String i8pwd;
    @Value("${i8.database}")
    private String i8database;
    @Value("${i8.ocode}")
    private String i8ocode;


    //缓冲时间 5小时  //1天
    private final static long EXPIRATIONTIME = 1000 * 60 * 60 * 5;
    public static Map<String, String> keySecretMap = new HashMap();
    public static Map<String, Long> keyTimeMap = new HashMap();

    /*
     *  校验 token 时间戳是否超时
     */
    private static boolean isInvalid(String timeKey) {
        //如果时间key为null或者map里没这个key就查数据库
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(timeKey) || !keyTimeMap.containsKey(timeKey))
            return false;
        Long expiryTime = keyTimeMap.get(timeKey);
        //如果当前时间大于缓存过期时间就移除map里的数据key
        if (System.currentTimeMillis() > expiryTime) {
            keyTimeMap.remove(timeKey);
            return false;
        }
        return true;
    }

    /**
     * form表单提交
     *
     * @param funcUrl
     * @param formdata
     * @return
     */
    public I8ReturnModel PostFormSync(String funcUrl, List<NameValuePair> formdata) {
        //  uipLog.info("orgsync", "日志226", "阶段" );
        return PostFormSyncByOcode(funcUrl, formdata, i8ocode);
    }

    /**
     * form表单提交
     *
     * @param funcUrl
     * @param formdata
     * @return
     */
    public I8ReturnModel PostFormSync(String funcUrl, List<NameValuePair> formdata, HashMap<String, Object>... header) {
        return PostFormSyncByOcode(funcUrl, formdata, i8ocode, header);
    }

    /**
     * 根据组织编码同步（获取）数据
     *
     * @param funcUrl
     * @param formdata
     * @param ocode
     * @return
     */
    public I8ReturnModel PostFormSyncByOcode(String funcUrl, List<NameValuePair> formdata, String ocode, HashMap<String, Object>... header) {
        if (StringUtils.isEmpty(ocode)) {
            ocode = i8ocode;
        }
        //获取token
        String ngToken = getTokenByOcode(ocode);
        I8ReturnModel i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken, header);
        if (!i8ReturnModel.getIsOk()) {
            keySecretMap.remove(ocode);
            ngToken = getTokenByOcode(ocode);
            i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken, header);
        }
        /*if (i8ReturnModel.getIsOk()) {
            return I8ResultUtil.getI8Return(i8ReturnModel.getData().toString());
        }*/
        return i8ReturnModel;
    }

    /**
     * POST
     *
     * @param postUrl
     * @param formdata
     * @param token
     * @return
     */
    private static I8ReturnModel sendPostStr(String postUrl, List<NameValuePair> formdata, String token, HashMap<String, Object>... header) {
        return sendFormPost(postUrl, formdata, token, header);
    }

    /**
     * POST
     *
     * @param postUrl
     * @param formdata
     * @param token
     * @return
     */
    private static I8ReturnModel sendFormPost(String postUrl, List<NameValuePair> formdata, String token, HashMap<String, Object>... header) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {
//            信任所有SSL证书
//            HttpsURLConnection.setDefaultHostnameVerifier(new I8Request().new NullHostNameVerifier());
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /* 1. 获取访问地址URL */
            URL url = new URL(postUrl);

            /* 2. 创建HttpURLConnection对象 */
            connection = (HttpURLConnection) url.openConnection();

            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置连接超时时间
            connection.setConnectTimeout(20000);
            // 设置读取超时时间
            connection.setReadTimeout(60000);
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 添加 HTTP HEAD 中的一些参数。
            // 设置请求格式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // JDK8中，HttpURLConnection默认开启Keep-Alive
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置请求Headers(Token)
            if (!StringUtils.isEmpty(token)) {
                connection.setRequestProperty("Authorization", token);
            }

            if (header != null && header.length != 0) {
                // 由于通常只传入一个header，所以直接处理第一个元素
                // 如果需要处理多个header，则应该遍历header数组
                HashMap<String, Object> firstHeader = header[0];

                // 现在可以安全地遍历并处理第一个header（如果它存在）
                for (Map.Entry<String, Object> entry : firstHeader.entrySet()) {
                    String key = entry.getKey();
                    String value = (String) entry.getValue();
                    // 这里进行你需要的处理，例如打印键值对
                    connection.setRequestProperty(key, value);
                }
            }

            /* 4. 处理输入输出 */
            StringBuilder params = new StringBuilder();
            // 表单参数与get形式一样
            for (NameValuePair v : formdata) {
                params.append(v.getName()).append("=").append(URLEncoder.encode(v.getValue(), "utf-8")).append("&");
            }
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            writer.write(params.toString());
            writer.flush();
            writer.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            //根据responseCode来获取输入流，此处错误响应码的响应体内容也要获取（看服务端的返回结果形式决定）
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                return I8ResultUtil.success("请求成功", result.toString());
            }
            return I8ResultUtil.error(String.valueOf(connection.getResponseCode()), result.toString());
        } catch (Exception ex) {
            return I8ResultUtil.error("请求异常：" + ex.getMessage());
        } finally {
            /* 5. 断开连接 */
            assert connection != null;
            connection.disconnect();
        }
    }

    /**
     * 根据操作员同步（获取）数据
     *
     * @param funcUrl
     * @param formdata
     * @param userNo
     * @return
     */
    public I8ReturnModel PostFormByUser(String funcUrl, List<NameValuePair> formdata, String userNo) {
        //获取token
        String ngToken = getTokenByUser(userNo);
        I8ReturnModel i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken);
        if (!i8ReturnModel.getIsOk()) {
            keySecretMap.remove(userNo);
            ngToken = getTokenByUser(userNo);
            i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken);
        }
        if (i8ReturnModel.getIsOk()) {
            return I8ResultUtil.getI8Return(i8ReturnModel.getData().toString());
        }
        return i8ReturnModel;
    }

    /**
     * 根据组织编码同步（获取）数据
     *
     * @param funcUrl
     * @param formdata
     * @param ocode
     * @return
     */
    public I8ReturnModel PostFormSyncByOcode(String funcUrl, List<NameValuePair> formdata, String ocode) {
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(ocode)) {
            ocode = i8ocode;
        }
        //获取token

        String ngToken = getTokenByOcode(ocode);
        I8ReturnModel i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken);
        if (!i8ReturnModel.getIsOk()) {
            keySecretMap.remove(ocode);
            ngToken = getTokenByOcode(ocode);
            i8ReturnModel = sendPostStr(i8url + funcUrl, formdata, ngToken);
        }
        if (i8ReturnModel.getIsOk()) {
            return I8ResultUtil.getI8Return(i8ReturnModel.getData().toString());
        }
        return i8ReturnModel;
    }

    /**
     * POST
     *
     * @param postUrl
     * @param formdata
     * @param token
     * @return
     */
    private static I8ReturnModel sendPostStr(String postUrl, List<NameValuePair> formdata, String token) {
        return sendFormPost(postUrl, formdata, token);
    }

    /**
     * POST
     *
     * @param postUrl
     * @param formdata
     * @param token
     * @return
     */
    private static I8ReturnModel sendFormPost(String postUrl, List<NameValuePair> formdata, String token) {

        StringBuilder result = new StringBuilder();

        HttpURLConnection connection = null;
        try {
//            信任所有SSL证书
//            HttpsURLConnection.setDefaultHostnameVerifier(new I8Request().new NullHostNameVerifier());
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /* 1. 获取访问地址URL */
            URL url = new URL(postUrl);

            /* 2. 创建HttpURLConnection对象 */
            connection = (HttpURLConnection) url.openConnection();

            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置连接超时时间
            connection.setConnectTimeout(20000);
            // 设置读取超时时间
            connection.setReadTimeout(60000);
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 添加 HTTP HEAD 中的一些参数。
            // 设置请求格式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // JDK8中，HttpURLConnection默认开启Keep-Alive
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置请求Headers(Token)
            if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(token)) {
                connection.setRequestProperty("Authorization", token);
            }

            /* 4. 处理输入输出 */
            StringBuilder params = new StringBuilder();
            // 表单参数与get形式一样
            for (NameValuePair v : formdata) {
                params.append(v.getName()).append("=").append(URLEncoder.encode(v.getValue(), "utf-8")).append("&");
            }
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            writer.write(params.toString());
            writer.flush();
            writer.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            //根据responseCode来获取输入流，此处错误响应码的响应体内容也要获取（看服务端的返回结果形式决定）
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                return I8ResultUtil.success("请求成功", result.toString());
            }
            return I8ResultUtil.error(String.valueOf(connection.getResponseCode()), result.toString());
        } catch (Exception ex) {
            log.info(postUrl + "，请求异常：" + ex.getMessage());
            return I8ResultUtil.error("请求异常：" + ex.getMessage());
        } finally {
            /* 5. 断开连接 */
            assert connection != null;
            connection.disconnect();
        }
    }


    /**
     * POST
     *
     * @param postUrl
     * @param body
     * @param token
     * @return
     */
    private static I8ReturnModel sendPost(String postUrl, String body, String token) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {
//            信任所有SSL证书
//            HttpsURLConnection.setDefaultHostnameVerifier(new I8Request().new NullHostNameVerifier());
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /* 1. 获取访问地址URL */
            URL url = new URL(postUrl);

            /* 2. 创建HttpURLConnection对象 */
            connection = (HttpURLConnection) url.openConnection();

            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置连接超时时间
            connection.setConnectTimeout(20000);
            // 设置读取超时时间
            connection.setReadTimeout(60000);
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 添加 HTTP HEAD 中的一些参数。
            // 设置请求格式
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // JDK8中，HttpURLConnection默认开启Keep-Alive
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置请求Headers(Token)
            if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(token)) {
                connection.setRequestProperty("Authorization", token);
            }

            /* 4. 处理输入输出 */
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            //根据responseCode来获取输入流，此处错误响应码的响应体内容也要获取（看服务端的返回结果形式决定）
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                return I8ResultUtil.success("请求成功", result.toString());
            }
            return I8ResultUtil.error(String.valueOf(connection.getResponseCode()), result.toString());
        } catch (Exception ex) {
            log.info(postUrl + "，请求异常：" + ex.getMessage());
            return I8ResultUtil.error("请求异常：" + ex.getMessage());
        } finally {
            /* 5. 断开连接 */
            connection.disconnect();
        }
    }

    /**
     * GET
     *
     * @param postUrl
     * @param formdata
     * @param token
     * @return
     */
    private static I8ReturnModel sendGet(String postUrl, List<NameValuePair> formdata, String token) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {
//            信任所有SSL证书
//            HttpsURLConnection.setDefaultHostnameVerifier(new I8Request().new NullHostNameVerifier());
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /* 1. 获取访问地址URL */
            URL url = new URL(postUrl);

            /* 2. 创建HttpURLConnection对象 */
            connection = (HttpURLConnection) url.openConnection();

            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置请求方式
            connection.setRequestMethod("GET");
            // 设置连接超时时间
            connection.setConnectTimeout(20000);
            // 设置读取超时时间
            connection.setReadTimeout(30000);
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否使用缓存
            connection.setUseCaches(true);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 添加 HTTP HEAD 中的一些参数。
            // JDK8中，HttpURLConnection默认开启Keep-Alive
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 设置请求Headers(Token)
            if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(token)) {
                connection.setRequestProperty("Authorization", token);
            }
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception ex) {
            log.info(postUrl + "，请求异常：" + ex.getMessage());
            return I8ResultUtil.error("请求异常：" + ex.getMessage());
        } finally {
            /* 5. 断开连接 */
            assert connection != null;
            connection.disconnect();
        }
        return I8ResultUtil.success("请求成功", result.toString());
    }

    private String getTokenByOcode(String ocode) {
        String ngToken = keySecretMap.get(ocode);
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(ngToken) || isInvalid(ngToken)) {
            ngToken = getToken(ocode, i8user);
            keySecretMap.put(ocode, ngToken);
            keyTimeMap.put(ngToken, System.currentTimeMillis() + EXPIRATIONTIME);
        }
        return ngToken;
    }

    private String getTokenByUser(String userNo) {
        String ngToken = keySecretMap.get(userNo);
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isEmpty(ngToken) || isInvalid(ngToken)) {
            ngToken = getToken(i8ocode, userNo);
            keySecretMap.put(userNo, ngToken);
            keyTimeMap.put(ngToken, System.currentTimeMillis() + EXPIRATIONTIME);
        }
        return ngToken;
    }

    private String getToken(String ocode, String userNo) {
        return getToken(i8url + "/api/KernelSession", ocode, userNo, "NG" + i8database);
    }

    /**
     * 获取产品 Token
     *
     * @param i8url
     * @param ocode
     * @param userNo
     * @param i8database
     * @return
     */
    private static String getToken(String i8url, String ocode, String userNo, String i8database) {
        List<NameValuePair> params = new ArrayList<>();
        //TODO 这里可能会拿不到token,建议捞出param直接网页端发起请求,有的项目只需要ucode就能拿到token
        params.add(new BasicNameValuePair("ucode", i8database));
        params.add(new BasicNameValuePair("ocode", ocode));
        params.add(new BasicNameValuePair("loginid", userNo));
        I8ReturnModel returnModel = sendFormPost(i8url, params, "");
        if (ObjectUtils.isNull(returnModel) || !returnModel.getIsOk()) {
            log.error("获取token失败！" + returnModel.getMessage());
            throw new RuntimeException("获取token失败！");
        }
        JSONObject data = JSONObject.parseObject(returnModel.getData().toString());
        if ("Success".equalsIgnoreCase(data.getString("status")) && "ApporgNotExist".equalsIgnoreCase(data.getString("errmsg"))) {
            throw new RuntimeException("获取token失败：无对应权限");
        }
        if ("UnSuccess".equalsIgnoreCase(data.getString("status"))) {
            throw new RuntimeException("获取token失败：" + data.getString("errmsg"));
        }
        return data.getString("accesstoken");
    }

    /**
     * 根据表名获取主键（产品提供）
     */
    public List<String> getPhIdList(String tableName, Integer needCount) {
        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("生成主键失败-TableName不能为空或者null");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("NeedCount", needCount);
        map.put("PrimaryName", "phid");
        map.put("Step", "1");
        map.put("TableName", tableName);
        map.put("CuOrOrgId", "0");

        I8ReturnModel returnModel = sendPost(i8url + "/api/common/billno/GetBillIdIncrease", JSONObject.toJSONString(map), getTokenByOcode(i8ocode));
        if (!returnModel.getIsOk()) {
            keySecretMap.remove(i8ocode);
            returnModel = sendPost(i8url + "/api/common/billno/GetBillIdIncrease", JSONObject.toJSONString(map), getTokenByOcode(i8ocode));
        }
        if (returnModel.getIsOk()) {
            if (JSONValid.isNotJSON2(returnModel.getData().toString())) {
                throw new RuntimeException("生成主键失败-" + returnModel.getData().toString());
            }
            JSONObject backData = JSONObject.parseObject(returnModel.getData().toString());
            return backData.getJSONArray("BillIdList").toJavaList(String.class);
        }
        throw new RuntimeException("生成主键失败-" + returnModel.getMessage());
    }
}