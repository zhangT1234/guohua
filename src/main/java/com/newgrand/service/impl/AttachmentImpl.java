package com.newgrand.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.webservice.SoapClient;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.config.ConfigValue;
import com.newgrand.domain.model.I8FileBlock;
import com.newgrand.domain.model.I8FileModel;
import com.newgrand.service.AttachmentService;
import com.newgrand.utils.i8util.I8FileUtil;
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
import org.dom4j.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 附件保存
     *
     * @param asr_guid 会话id
     * @param asr_code 单据id
     * @param asr_mode 是否异步 0 同步 1 异步
     * @return
     */
    @Override
    public boolean save(String asr_guid, String asr_code, String asr_mode) throws DocumentException {
        SoapClient soapClient = SoapClient.create(configValue.i8Url+"/filesrv/UploadFileService.asmx?wsdl")
                .setCharset(CharsetUtil.CHARSET_UTF_8)
                // 设置调用方法名称以及命名空间
                .setMethod("Save", "http://tempuri.org/")
                // 设置参数名称及参数值
                .setParam("asr_guid", asr_guid)
                .setParam("asr_code", asr_code)
                .setParam("asr_mode", "0")
                // 设置超时时间
                .setConnectionTimeout(15000)
                .setReadTimeout(15000)
                .timeout(15000);

        // 调用webservice接口
        String result = soapClient.send();
        Document document = DocumentHelper.parseText(result);
        //指向根节点
        Element root2 = document.getRootElement();
        String resData = root2.element("Body").element("SaveResponse").elementText("SaveResult");
        if ("1".equals(resData)) {
            return true;
        }
        throw new RuntimeException(resData);
    }

    /**
     * 附件保存
     */
    @Override
    public boolean upLoadFile(I8FileModel i8FileModel) {
        SoapClient soapClient = SoapClient.create("http://61.175.201.70:8889/filesrv/UploadFileService.asmx?wsdl")
                .setCharset(CharsetUtil.CHARSET_UTF_8)
                // 设置调用方法名称以及命名空间
                .setMethod("SaveData", "http://tempuri.org/")
                // 设置参数名称及参数值
                .setParam("asr_guid", "")
                .setParam("asr_code", i8FileModel.getAsr_code())
                .setParam("asr_table", i8FileModel.getAsr_table())
                .setParam("asr_attach_table", i8FileModel.getAsr_attach_table())
                .setParam("asr_dbconn", "ConnectType=SqlClient;Server=10.0.20.14:1433;Database=NG0001;User Id=sa;Password=Asd@123")
                .setParam("asr_params", i8FileModel.getAsr_fillname())
                .setParam("asr_data", i8FileModel.getAsr_data_base64())
                .setParam("asr_name", i8FileModel.getAsr_fillname())
                .setParam("approved", "")
                .setParam("containerid", "")
                // 设置超时时间
                .setConnectionTimeout(15000)
                .setReadTimeout(15000)
                .timeout(15000);


        try {
            // 调用webservice接口
            String result = soapClient.send();
//            Document document = DocumentHelper.parseText(result);
//            //指向根节点
//            Element root2 = document.getRootElement();
//            String resData = root2.element("Body").element("SaveResponse").elementText("SaveResult");
//            if ("1".equals(resData)) {
//                return true;
//            }
             if (result!=null && result.contains("<SaveDataResult>1</SaveDataResult>")) {
                 return true;
             }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 附件数据分片调用webservice
     *
     * @param asr_session_guid 附件uuid（唯一）
     * @param asr_data         附件二进制
     * @param fileid           附件id
     * @param curpart          当前片数
     * @param totalparts       附件总片数
     * @param filemd5          附件mde5
     * @param filesize         附件长度
     * @param asr_part_size    分片大小
     * @param isAppUpload      是否app 默认 0
     * @return
     */
    @Override
    public boolean blockUpload(String asr_session_guid, String asr_data, String fileid, String filename, Integer curpart, Integer totalparts, String filemd5, String filesize, Integer asr_part_size, String isAppUpload) throws DocumentException {
        SoapClient soapClient = SoapClient.create(configValue.i8Url+"/filesrv/UploadFileService.asmx?wsdl")
                .setCharset(CharsetUtil.CHARSET_UTF_8)
                // 设置调用方法名称以及命名空间
                .setMethod("BlockUpload", "http://tempuri.org/")
                // 设置参数名称及参数值
                .setParam("asr_session_guid", asr_session_guid)
                .setParam("asr_data", asr_data)
                .setParam("filename", filename)
                .setParam("fileid", fileid)
                .setParam("curpart", curpart)
                .setParam("totalparts", totalparts)
                .setParam("filemd5", filemd5)
                .setParam("filesize", filesize)
                .setParam("asr_part_size", asr_part_size)
                .setParam("isAppUpload", isAppUpload)
                // 设置超时时间
                .setConnectionTimeout(15000)
                .setReadTimeout(15000)
                .timeout(15000);

        // 调用webservice接口
        String result = soapClient.send();
        Document document = DocumentHelper.parseText(result);
        //指向根节点
        Element root2 = document.getRootElement();
        String resData = root2.element("Body").element("BlockUploadResponse").elementText("BlockUploadResult");
        if ("1".equals(resData)) {
            return true;
        }
        throw new RuntimeException("附件分片上传失败：" + resData);
    }

    @Override
    public boolean postFileItem(I8FileModel data) {
        try {
            Boolean initialize = initEx(data.getAsr_session_guid(), data.getAsr_attach_table(),
                    data.getAsr_code(), data.getAsr_table(), data.getAsr_fill(), data.getAsr_fillname());
            if (!initialize) {
                throw new RuntimeException("附件初始化失败");
            }
            log.error("附件初始化成功：" + data.getAsr_session_guid() + "," + data.getAsr_table());
            //logService.info("postFileItem", "附件初始化成功", data.getAsr_session_guid(), data.getAsr_table());
            if (data.getAsr_data().length == 0) {
                throw new RuntimeException("附件二进制数据不能为空");
            }
            List<I8FileBlock> blockList = I8FileUtil.getFileBlock(data.getAsr_session_guid(), data.getAsr_fillname(), data.getAsr_data());
            if (blockList.size() == 0) {
                throw new RuntimeException("附件二进制数据分片异常");
            }
            log.error("附件二进制数据分片成功，分片数据条数：" + blockList.size() + "," + data.getAsr_table() + "," + data.getAsr_table());
            //logService.info("postFileItem", "附件二进制数据分片成功", "分片数据条数：" + blockList.size(), data.getAsr_session_guid(), data.getAsr_table());
            for (I8FileBlock item : blockList) {
                blockUpload(item.getAsr_session_guid(), item.getAsr_data(),
                        item.getFileid(), item.getFilename(), item.getCurpart(), item.getTotalParts(), item.getFilemd5(),
                        item.getFilesize(), item.getAsr_part_size(), "0");
            }
            log.error("附件分片上传成功：" + data.getAsr_table() + "," + data.getAsr_table());
            //logService.info("postFileItem", "附件分片上传成功", data.getAsr_session_guid(), data.getAsr_table());
            save(data.getAsr_session_guid(), data.getAsr_code(), "0");
            log.error("附件上传成功：" + data.getAsr_table() + "," + data.getAsr_table());
            //logService.info("postFileItem", "附件上传成功", data.getAsr_session_guid(), data.getAsr_table());
            return true;
        } catch (Exception ex) {
            log.error("附件上传失败：" + ex.getMessage());
            //logService.error("postFileItem", "附件上传失败", ex.getMessage(), data.getAsr_session_guid(), data.getAsr_table());
            return false;
        }
    }

    /**
     * 附件分片上传初始化
     *
     * @param asr_session_guid uuid（唯一）
     * @param asr_attach_table 单据附件表
     * @param asr_code         单据id
     * @param asr_table        单据表名
     * @param asr_fill         操作员
     * @param asr_fillname     附件名称
     * @return
     */
    @Override
    public boolean initEx(String asr_session_guid, String asr_attach_table, String asr_code, String asr_table, String asr_fill, String asr_fillname) throws DocumentException {
        SoapClient soapClient = SoapClient.create(configValue.i8Url+"/filesrv/UploadFileService.asmx?wsdl")
                .setCharset(CharsetUtil.CHARSET_UTF_8)
                // 设置调用方法名称以及命名空间
                .setMethod("InitEx", "http://tempuri.org/")
                // 设置参数名称及参数值
                .setParam("asr_session_guid", asr_session_guid)
                .setParam("asr_attach_table", asr_attach_table)
                .setParam("asr_table", asr_table)
                .setParam("asr_code", asr_code)
                .setParam("asr_fill", asr_fill)
                .setParam("asr_fillname", asr_fillname)
                .setParam("asr_dbconn", configValue.dbConnect)
                // 设置超时时间
                .setConnectionTimeout(15000)
                .setReadTimeout(15000)
                .timeout(15000);

        // 调用webservice接口
        String result = soapClient.send();
        Document document = DocumentHelper.parseText(result);
        //指向根节点
        Element root2 = document.getRootElement();
        String InitExResult = root2.element("Body").element("InitExResponse").elementText("InitExResult");
        if (asr_session_guid.equals(InitExResult)) {
            return true;
        }
        throw new RuntimeException(InitExResult);
    }

}
