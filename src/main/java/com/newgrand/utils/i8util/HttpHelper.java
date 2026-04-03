package com.newgrand.utils.i8util;

import ch.qos.logback.classic.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/12 16:13
 * @Description: TODO
 */
@Service
public class HttpHelper {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(HttpHelper.class);

    public static String Post(String action, HttpEntity parameter, Header[] headers) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(action);

            if (parameter != null)
                httpPost.setEntity(parameter);

            if (headers != null)
                httpPost.setHeaders(headers);

            response = httpClient.execute(httpPost);
            System.out.println("获取请求结果：" + response.toString());
            System.out.println("获取请求结果码：" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return null;
    }

    public static String Get(String action, Header[] headers) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(action);

            if (headers != null)
                httpGet.setHeaders(headers);

            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, "utf-8");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return null;
    }
}

