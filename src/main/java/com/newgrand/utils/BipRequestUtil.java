package com.newgrand.utils;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BipRequestUtil {
    @Value("${bip.url}")
    private String bipUrl;

    public static final String AppKey = "appKey";

    public static final String TIMESTAMP_NAME="timestamp";
    public static final String SIGNATURE_NAME="signature";
    public static final String AppKeyValue="ce40a4c52975402d89ebaffa53a83bf3";
    public static final String AppSecretValue="7ee405d9248db110ed499947f8f4cdbfc45db5ca";

    public BipResult sendPost(String postUrl, String body) throws Exception {
        String token = getToken(bipUrl);
        if(token == null) {
            return BipResult.builder().code("400").message("获取token失败！").build();
        }
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {

            /* 1. 获取访问地址URL */
            URL url = new URL(bipUrl + postUrl + "?access_token=" + token);

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
                JSONObject resultJSON = JSONObject.parseObject(result.toString());
                return BipResult.builder()
                        .code(resultJSON.getString("code"))
                        .message(resultJSON.getString("message"))
                        .data(resultJSON.getJSONObject("data"))
                        .build();
            }
            return BipResult.builder().code("400").message("请求发送失败！").build();
        } catch (Exception ex) {
            log.info(postUrl + "，请求异常：" + ex.getMessage());
            return BipResult.builder().code("400").message(ex.getMessage()).build();
        } finally {
            /* 5. 断开连接 */
            connection.disconnect();
        }
    }

    private static String getToken(String bipUrl) throws Exception {
        long datetime = System.currentTimeMillis();

        String signatureReqStr= AppKey+AppKeyValue+ TIMESTAMP_NAME+datetime;
        Mac sha256_HMAC ;
        String signature = "";
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(AppSecretValue.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            signature = Base64.encodeBase64String(sha256_HMAC.doFinal(signatureReqStr.getBytes()));
            signature = URLEncoder.encode(signature,"utf-8");
        } catch (Exception e) {
            String msg="获取加密算法失败:appKey:"+AppKeyValue;
            throw new RuntimeException(msg);
        }

        String param= AppKey+"="+AppKeyValue+"&"+ TIMESTAMP_NAME+"="+datetime+"&"+ SIGNATURE_NAME+"="+signature;
        String url = bipUrl +"/iuap-api-auth/open-auth/selfAppAuth/getAccessToken"+"?"+ param;

        String tokenResult = sendGet(url);
        log.info(tokenResult);
        JSONObject tokenResultJSON = JSONObject.parseObject(tokenResult);
        if(tokenResultJSON.containsKey("code") && "00000".equals(tokenResultJSON.getString("code"))) {
            return tokenResultJSON.getJSONObject("data").getString("access_token");
        }
        return null;
    }

    public static String sendGet(String url) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置传递方式
            connection.setRequestMethod("GET");
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            logger.error("error!发送GET请求出现异常: "+ExceptionPrintMessage.errorTrackSpace(e));
            throw new Exception("发送GET请求出现异常:"+e.getMessage(), e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
