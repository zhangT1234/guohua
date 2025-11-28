package com.newgrand.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.config.ConfigValue;
import com.newgrand.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 附件服务
 *
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 17:38
 */
@Slf4j
@Service
public class AttachmentImpl implements AttachmentService {


    static int socketTimeout = 500000;// 请求超时时间
    static int connectTimeout = 500000;// 传输超时时间

    @Resource
    private ConfigValue configValue;

    @Override
    public void downLoad(HttpServletResponse httpServletResponse, String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException {
        log.info("downloadProjectBlock", String.format("来源table: %s, 文件名称: %s", asr_table, asr_filename));

        String getUrl = configValue.i8Url + "/filesrv/UploadFileService.asmx";
        String xmlTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <tem:GetFileBlockCount>\n" +
                "        <tem:asr_session_guid></tem:asr_session_guid>\n" +
                "        <tem:asr_code>" + asr_code + "</tem:asr_code>\n" +
                "        <tem:asr_table>" + asr_table + "</tem:asr_table>\n" +
                "        <tem:asr_attach_table>" + asr_attach_table + "</tem:asr_attach_table>\n" +
                "        <tem:asr_name>" + asr_filename + "</tem:asr_name>\n" +
                "        <tem:asr_dbconn>" + configValue.dbConnect + "</tem:asr_dbconn>\n" +
                "      </tem:GetFileBlockCount>\n" +
                "    </soapenv:Body>\n" +
                " </soapenv:Envelope>";
        String returnBody = postGetBlockCount(getUrl, xmlTemplate);
        if (StringUtils.isEmpty(returnBody)) {
            log.warn("downloadProjectBlock", String.format("来源table: %s, 文件名称: %s 下载失败", asr_table, asr_filename));
        }

        JSONObject body = JSONObject.parseObject(returnBody);
        String success = body.getString("success");
        String asrsessionguid = body.getString("asrsessionguid");
        String asrfid = body.getString("asrfid");
        String count = body.getString("count");
        int counti = Integer.parseInt(count);
        byte[] decodedBytes = new byte[0];
        if ("1".equals(success)) {
            for (int i = 0; i < counti; i++) {
                String xmlBlock = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                        "   <soapenv:Header/>\n" +
                        "   <soapenv:Body>\n" +
                        "      <tem:GetFileBlock>\n" +
                        "        <tem:asr_session_guid>" + asrsessionguid + "</tem:asr_session_guid>\n" +
                        "        <tem:asr_fid>" + asrfid + "</tem:asr_fid>\n" +
                        "        <tem:asr_seq>" + i + "</tem:asr_seq>\n" +
                        "        <tem:asr_dbconn>" + configValue.dbConnect + "</tem:asr_dbconn>\n" +
                        "      </tem:GetFileBlock>\n" +
                        "    </soapenv:Body>\n" +
                        " </soapenv:Envelope>";
                decodedBytes = addBytes(decodedBytes, postGetBlock(getUrl, xmlBlock));
            }
        }

        //var decodedBytes = attachmentHandler.post(getUrl, xmlTemplate);
        //设置响应体保证自动下载
        httpServletResponse.setHeader("content-type", "application/octet-stream");
        httpServletResponse.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(asr_filename, "UTF-8"));
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        if (decodedBytes != null) {
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            byte[] c = new byte[1024]; //缓冲
            int length;
            while ((length = inputStream.read(c)) > 0) { //将数据读入缓冲
                servletOutputStream.write(c, 0, length); //将缓冲写入返回
            }
        } else {
            log.warn("downloadProjectBlock", String.format("来源table: %s, 文件名称: %s 不存在", asr_table, asr_filename));
        }

        servletOutputStream.flush();
        servletOutputStream.close();
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    /**
     * 使用post请求登录来提取文件byte数组
     * 所用参数是通过接口文档确定的
     *
     * @param postUrl          webservice地址
     * @param soapXml          发送的xml模板
     * @param asr_code         附件所属数据条目phid
     * @param asr_table        附件所属table名称
     * @param asr_attach_table 默认asr_info
     * @param asr_filename     附件文件名称
     * @return 文件二进制数据数组
     */
    public byte[] postGetFileByte(String postUrl, String soapXml, String asr_code,
                                  String asr_table, String asr_attach_table, String asr_filename) throws IOException {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        //设置请求参数
        soapXml = soapXml.replace("{asr_code}", asr_code);
        soapXml = soapXml.replace("{asr_table}", asr_table);
        soapXml = soapXml.replace("{asr_attach_table}", asr_attach_table);
        soapXml = soapXml.replace("{asr_filename}", asr_filename);
        soapXml = soapXml.replace("{dbConn}", configValue.dbConnect);//环境变量 设置数据库连接


        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 提取文件数据
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                //根据原来.net代码翻译而来
                int start = retStr.indexOf("<GetResult>");
                int end = retStr.indexOf("</GetResult>");
                //System.out.println(retStr.substring(start + 11,end));
                if (start == -1 || end == -1) {
                    log.warn("download", "请求WS文件没有返回文件" + "ws没有返回文件");
                    return null;
                } else {
                    String rawFile = retStr.substring(start + 11, end);
                    return Base64.getDecoder().decode(rawFile); //解码成8bit
                }
            } else {
                log.warn("download", "请求WS文件没有返回值" + "ws没有返回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("download", e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public boolean postSaveFile(String postUrl, String soapXml) {
        String retStr = "";
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                // 提取文件数据
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
                //根据原来.net代码翻译而来
                int start = retStr.indexOf("<SaveDataResult>");
                int end = retStr.indexOf("</SaveDataResult>");
                // System.out.println(retStr.substring(start + 11,end));
                // System.out.println(retStr);
                if (start == -1 || end == -1) {
                    log.warn("download", "请求WS上传失败" + "ws没有返回值");
                    return false;
                } else {
                    String result = retStr.substring(start + "<SaveDataResult>".length(), end);
                    //System.out.println(String.format("result: %s", result));
                    if (result.equals("1")) {
                        return true;
                    } else {
                        log.warn("download", "请求WS上传失败" + "ws返回上传失败");
                        return false;
                    }
                }
            } else {
                log.warn("download", "请求WS文件没有返回值" + "ws没有返回");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("download", e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取附件块
     *
     * @param postUrl
     * @param soapXml
     * @return
     */
    public String postGetBlockCount(String postUrl, String soapXml) {
        String retStr;
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10 * 60 * 60).build());
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                log.warn("download", "ws没有返回");
                return null;
            }
            // 提取文件数据
            retStr = EntityUtils.toString(httpEntity, "UTF-8");
            //根据原来.net代码翻译而来
            int start = retStr.indexOf("<GetFileBlockCountResult>");
            int end = retStr.indexOf("</GetFileBlockCountResult>");
            //System.out.println(retStr.substring(start + 11,end));
            if (start == -1 || end == -1) {
                log.warn("download", "ws没有返回文件");
                return null;
            } else {
                String rawFile = retStr.substring(start + 25, end);
                return rawFile; //解码成8bit
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("download", e.getMessage());
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取附件流块
     *
     * @param postUrl
     * @param soapXml
     * @return
     */
    public byte[] postGetBlock(String postUrl, String soapXml) {
        String retStr;
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        try {
            httpPost.setHeader("Content-Type", "application/xml");
            StringEntity data = new StringEntity(soapXml, StandardCharsets.UTF_8);
            httpPost.setEntity(data);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10 * 60 * 60).build());
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                log.warn("download", "ws没有返回");
                return null;
            }
            // 提取文件数据
            retStr = EntityUtils.toString(httpEntity, "UTF-8");
            //根据原来.net代码翻译而来
            int start = retStr.indexOf("<GetFileBlockResult>");
            int end = retStr.indexOf("</GetFileBlockResult>");
            if (start == -1 || end == -1) {
                log.warn("download", "ws没有返回文件");
            } else {
                String rawFile = retStr.substring(start + 20, end);
                return Base64.getDecoder().decode(rawFile); //解码成8bit
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (closeableHttpClient != null)
                    closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
