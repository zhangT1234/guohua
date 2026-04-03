package com.newgrand.utils;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.dto.OaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class OaRequestUtil {

    @Value("${oa.url}")
    private String oaUrl;

    public OaResult sendPost(String postUrl, String body) throws Exception {

        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            /* 1. 获取访问地址URL */
            String urlStr = oaUrl + postUrl;
            System.out.println("请求地址：" + urlStr);
            System.out.println("请求参数：" + body);
            URL url = new URL(urlStr);

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
                System.out.println("请求返回结果：" + result.toString());
                JSONObject resultJSON = JSONObject.parseObject(result.toString());
                return OaResult.builder()
                        .code(resultJSON.getString("code"))
                        .msg(resultJSON.getString("msg"))
                        .data(resultJSON.getString("data")!=null?resultJSON.getString("data"):"")
                        .build();
            }
            return OaResult.builder().code("400").msg("请求发送失败！").build();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info(postUrl + "，请求异常：" + ex.getMessage());
            return OaResult.builder().code("400").msg(ex.getMessage()).build();
        } finally {
            /* 5. 断开连接 */
            connection.disconnect();
        }
    }

}
