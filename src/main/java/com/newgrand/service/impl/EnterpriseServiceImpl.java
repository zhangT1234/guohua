package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.model.EnterpriseFileModel;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.model.FileModel;
import com.newgrand.domain.model.I8FileModel;
import com.newgrand.service.AttachmentService;
import com.newgrand.service.EnterpriseService;
import com.newgrand.service.Fg3EnterpriseService;
import com.newgrand.utils.StringUtils;
import com.newgrand.utils.i8util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Fg3EnterpriseService fg3EnterpriseService;
    @Autowired
    private HttpHelper httpHelper;
    @Autowired
    private AttachmentService attachmentService;

    //同步供应商附件, 银行账号, 开户行
    //@Transactional
    public void syncEnterpriseAttachment(String unisocialCredit) {
        LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
       //String unisocialCredit = "92330205MA2ELNR025";
        if (StringUtils.isNotBlank(unisocialCredit)) {
           queryWrapper.eq(Fg3Enterprise::getUnisocialCredit, unisocialCredit);
        }
        queryWrapper.eq(Fg3Enterprise::getFromtype, "supply");
        List<Fg3Enterprise> list  = fg3EnterpriseService.list(queryWrapper);
        List<Map<String, Object>> bankInfo = jdbcTemplate.queryForList("SELECT phid,bankname FROM fg_bank");
        for (Fg3Enterprise fg3Enterprise : list) {
            updateEnterpriseAttachment(fg3Enterprise, bankInfo);
        }
    }

    //@Transactional
    public void updateEnterpriseAttachment(Fg3Enterprise fg3Enterprise,  List<Map<String, Object>> bankInfo) {
            try {
                if (StringUtils.isNotBlank(fg3Enterprise.getUnisocialCredit())) {
                    EnterpriseFileModel enterpriseFileModel = getAttachmentInfo(fg3Enterprise.getUnisocialCredit());
                    String bankId = null;
                    for (Map<String, Object> map : bankInfo) {
                        if( map.get("bankname")!=null && map.get("bankname").equals(enterpriseFileModel.getOpenBank())){
                            bankId =  map.get("phid")!=null?map.get("phid").toString():null;
                        }
                    }
                    //更新银行账号, 开户行
                    log.info("更新银行账号, 开户行");
                    jdbcTemplate.update("UPDATE fg3_supplysettleinfo set bank_id = ? , accountno = ? , user_khyh = ? where ent_id = ?", bankId, enterpriseFileModel.getBankAccount(), enterpriseFileModel.getOpenBank(), fg3Enterprise.getPhid());
                    log.info("更新银行账号, 开户行成功");
                    //获取附件主键
                    List<Map<String, Object>> supplyFile = jdbcTemplate.queryForList("SELECT phid FROM fg3_SUPPLYFILE WHERE ent_id= ?", fg3Enterprise.getPhid());
                    String attachmentId = null;
                    if (CollectionUtil.isNotEmpty(supplyFile)) {
                        attachmentId = supplyFile.get(0).get("phid").toString();
                    }
                    //测试
                    //attachmentId = "823000000002504";
                    //同步附件
                    Map<String, String> aMap = new HashMap<>();
                    if (enterpriseFileModel.getBankFileInfo()!=null) {
                        //开户信息
                        //typeId 823000000000006
                        upLoadFile(attachmentId, enterpriseFileModel.getBankFileInfo());
                        //更新附件类型
                        log.info("更新开户信息附件类型");
                        jdbcTemplate.update("update attachment_record set typeid= ? where asr_table='fg3_supplyfile' and asr_code= ? and asr_name= ?", "823000000000006", attachmentId, enterpriseFileModel.getBankFileInfo().getOrgName());
                    }
                    if (enterpriseFileModel.getFileInfo()!=null) {
                        //营业职照
                        //typeId 823000000000004
                        upLoadFile(attachmentId, enterpriseFileModel.getFileInfo());
                        //更新附件类型
                        log.info("更新营业职照附件类型");
                        jdbcTemplate.update("update attachment_record set typeid= ? where asr_table='fg3_supplyfile' and asr_code= ? and asr_name= ?", "823000000000004", attachmentId, enterpriseFileModel.getFileInfo().getOrgName());
                    }
                    if (CollectionUtil.isNotEmpty(enterpriseFileModel.getSuppliperFileList())) {
                        //身份证
                        //typeId 823000000000005
                        for (FileModel fileModel : enterpriseFileModel.getSuppliperFileList()) {
                            upLoadFile(attachmentId, fileModel);
                            //更新附件类型
                            log.info("更新身份证附件类型");
                            jdbcTemplate.update("update attachment_record set typeid= ? where asr_table='fg3_supplyfile' and asr_code= ? and asr_name= ?", "823000000000005", attachmentId, fileModel.getOrgName());
                        }
                    }
                    if (CollectionUtil.isNotEmpty(enterpriseFileModel.getOtherFileList())) {
                        for (FileModel fileModel : enterpriseFileModel.getOtherFileList()) {
                            upLoadFile(attachmentId, fileModel);
                        }
                    }
                } else {
                    log.info("税号为空！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void upLoadFile(String attachmentId, FileModel fileModel) {
        String path = encodeUrlPath(fileModel.getPath());
        String fileUrl = "http://183.136.147.51:9710/" + path;
        String dirPath = "D:\\guohua\\file\\" + attachmentId;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
        }
        String targetFile = "D:\\guohua\\file\\" + attachmentId + "\\" + fileModel.getOrgName();
        HttpURLConnection connection = null;
        try{
             URL url = new URL(fileUrl);
             connection = (HttpURLConnection) url.openConnection();
             BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
             byte[] dataBuffer = new byte[1024];
             int bytesRead;
             while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
             }
             System.out.println("文件下载完成: " + targetFile);
        }catch(Exception e) {
             e.printStackTrace();
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
        }
        //附件上传
        uploadAttachmentInfo(attachmentId, fileModel.getOrgName(), targetFile);
    }

    //获取第三方附件信息
    public EnterpriseFileModel getAttachmentInfo(String unisocialCredit){
        String param = "{\n" +
                "    \"method\": \"com.ase.framework.handler.EntityPublicJsonRpcHandler.list\",\n" +
                "    \"params\": {\n" +
                "        \"webQueryInfo\": {\n" +
                "            \"skip\": 0,\n" +
                "            \"take\": 10,\n" +
                "            \"orderByField\": \"\",\n" +
                "            \"desc\": \"\",\n" +
                "            \"queryCriteriaList\": [\n" +
                "                {\n" +
                "                    \"fieldName\": \"taxIdentification\",\n" +
                "                    \"operators\": \"=\",\n" +
                "                    \"value\": \"unisocialCredit\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"includeFieldList\": [\n" +
                "                   \"bankFileInfo\",\n" +
                "                   \"fileInfo\",\n" +
                "                   \"suppliperFileList\",\n" +
                "                   \"otherFileList\"]\n" +
                "        },\n" +
                "        \"pageId\": \"970399341668286464\"\n" +
                "    },\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"id\": 0,\n" +
                "    \"token\": \"44badd2e-f634-48df-b0f6-31d8b806b443\"\n" +
                "}";
        param = param.replace("unisocialCredit",  unisocialCredit);
        String url = "http://183.136.147.51:9710/JsonRpc";
        StringEntity entity = new StringEntity(param, Charset.forName("UTF-8"));
        entity.setContentType("application/json");
        Header[] headers = new Header[1];
        Header header = new BasicHeader("Content-Type", "application/json");
        headers[0] = header;
        String rtnMsg = httpHelper.Post(url, entity, headers);
        log.info("税号：" + unisocialCredit + " 获取第三方附件信息返回rtnMsg：" + rtnMsg );
        JSONObject joRtn = JSONObject.parseObject(rtnMsg);
        List<FileModel> suppliperFileList = new ArrayList<>();
        List<FileModel> otherFileList = new ArrayList<>();
        EnterpriseFileModel  enterpriseFileModel = new EnterpriseFileModel();
        // 获取 bankAccount 字段
        JSONObject result = joRtn.getJSONObject("result");
        if (result!=null) {
            JSONArray items = result.getJSONArray("items");
            if (items!=null && items.size()>0) {
                JSONObject item = items.getJSONObject(0);
                if (item!=null) {
                    String bankAccount = item.getString("bankAccount");
                    String openBank = item.getString("openBank");
                    enterpriseFileModel.setBankAccount(bankAccount);
                    enterpriseFileModel.setOpenBank(openBank);

                    JSONObject bankFileInfo = item.getJSONObject("bankFileInfo");
                    if (bankFileInfo!=null) {
                        JSONObject jsonObj = (JSONObject) bankFileInfo;
                        String fullPath = jsonObj.get("fullPath")!=null?jsonObj.getString("fullPath"):"";
                        String orgName = jsonObj.get("orgName")!=null?jsonObj.getString("orgName"):"";
                        String urlPath = jsonObj.get("urlPath")!=null?jsonObj.getString("urlPath"):"";
                        String path = jsonObj.get("path")!=null?jsonObj.getString("path"):"";
                        String size = jsonObj.get("size")!=null?jsonObj.getString("size"):"";
                        String name = jsonObj.get("name")!=null?jsonObj.getString("name"):"";
                        FileModel fileModel = new FileModel();
                        fileModel.setFullPath(fullPath);
                        fileModel.setOrgName(orgName);
                        fileModel.setSize(size);
                        fileModel.setUrlPath(urlPath);
                        fileModel.setPath(path);
                        fileModel.setName(name);
                        enterpriseFileModel.setBankFileInfo(fileModel);
                    }

                    JSONObject fileInfo = item.getJSONObject("fileInfo");
                    if (fileInfo!=null) {
                        JSONObject jsonObj = (JSONObject) fileInfo;
                        String fullPath = jsonObj.get("fullPath")!=null?jsonObj.getString("fullPath"):"";
                        String orgName = jsonObj.get("orgName")!=null?jsonObj.getString("orgName"):"";
                        String urlPath = jsonObj.get("urlPath")!=null?jsonObj.getString("urlPath"):"";
                        String path = jsonObj.get("path")!=null?jsonObj.getString("path"):"";
                        String size = jsonObj.get("size")!=null?jsonObj.getString("size"):"";
                        String name = jsonObj.get("name")!=null?jsonObj.getString("name"):"";
                        FileModel fileModel = new FileModel();
                        fileModel.setFullPath(fullPath);
                        fileModel.setOrgName(orgName);
                        fileModel.setSize(size);
                        fileModel.setUrlPath(urlPath);
                        fileModel.setPath(path);
                        fileModel.setName(name);
                        enterpriseFileModel.setFileInfo(fileModel);
                    }

                    JSONArray suppliperFiles = item.getJSONArray("suppliperFileList");
                    if (suppliperFiles!=null) {
                        for (Object obj : suppliperFiles) {
                            JSONObject jsonObj = (JSONObject) obj;
                            String fullPath = jsonObj.get("fullPath")!=null?jsonObj.getString("fullPath"):"";
                            String orgName = jsonObj.get("orgName")!=null?jsonObj.getString("orgName"):"";
                            String urlPath = jsonObj.get("urlPath")!=null?jsonObj.getString("urlPath"):"";
                            String path = jsonObj.get("path")!=null?jsonObj.getString("path"):"";
                            String size = jsonObj.get("size")!=null?jsonObj.getString("size"):"";
                            String name = jsonObj.get("name")!=null?jsonObj.getString("name"):"";
                            FileModel fileModel = new FileModel();
                            fileModel.setFullPath(fullPath);
                            fileModel.setOrgName(orgName);
                            fileModel.setSize(size);
                            fileModel.setUrlPath(urlPath);
                            fileModel.setPath(path);
                            fileModel.setName(name);
                            suppliperFileList.add(fileModel);
                        }
                    }

                    JSONArray otherFiles = item.getJSONArray("otherFileList");
                    if (otherFiles!=null) {
                        for (Object obj : otherFiles) {
                            JSONObject jsonObj = (JSONObject) obj;
                            String fullPath = jsonObj.get("fullPath")!=null?jsonObj.getString("fullPath"):"";
                            String orgName = jsonObj.get("orgName")!=null?jsonObj.getString("orgName"):"";
                            String urlPath = jsonObj.get("urlPath")!=null?jsonObj.getString("urlPath"):"";
                            String path = jsonObj.get("path")!=null?jsonObj.getString("path"):"";
                            String size = jsonObj.get("size")!=null?jsonObj.getString("size"):"";
                            String name = jsonObj.get("name")!=null?jsonObj.getString("name"):"";
                            FileModel fileModel = new FileModel();
                            fileModel.setFullPath(fullPath);
                            fileModel.setOrgName(orgName);
                            fileModel.setSize(size);
                            fileModel.setUrlPath(urlPath);
                            fileModel.setPath(path);
                            fileModel.setName(name);
                            otherFileList.add(fileModel);
                        }
                    }
                }
            } else {
                log.info("没有附件数据,税号：" + unisocialCredit);
            }
        }
        enterpriseFileModel.setSuppliperFileList(suppliperFileList);
        enterpriseFileModel.setOtherFileList(otherFileList);
        return enterpriseFileModel;
    }

    //上传附件
    public void uploadAttachmentInfo(String asrCode, String fileName, String filePath){
       //File file = new File("/Users/xutianyu/Downloads/888.png");
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            I8FileModel data = new I8FileModel();
            UUID uuid = UUID.randomUUID();
            data.setAsr_session_guid(uuid.toString());
            data.setAsr_attach_table("c_pfc_attachment");
            data.setAsr_table("fg3_supplyfile");
            //单据主键
           // data.setAsr_code("823000000002504");
            data.setAsr_code(asrCode);
            data.setAsr_fillname("asr_name=" + fileName + "&asr_fill=315211029000006&asr_fillname=9997");
            data.setAsr_data(bytes);
            String base64Encoded = Base64.getEncoder().encodeToString(bytes);
            data.setAsr_data_base64(base64Encoded);
            boolean result = attachmentService.upLoadFile(data);
            System.out.println("文件上传结果：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis!=null) {
                try {
                    fis.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String encodeUrlPath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        try {
            // 分割路径段
            String[] segments = path.split("/");
            StringBuilder encodedPath = new StringBuilder();

            for (int i = 0; i < segments.length; i++) {
                String segment = segments[i];
                if (!segment.isEmpty()) {
                    // 对每一段进行 URL 编码
                    String encodedSegment = URLEncoder.encode(segment, StandardCharsets.UTF_8.name());
                    // 将 + 替换为 %20（URL 路径中空格应为 %20）
                    encodedSegment = encodedSegment.replace("+", "%20");
                    encodedPath.append(encodedSegment);
                }
                // 添加斜杠，但不在最后添加多余的斜杠
                if (i < segments.length - 1 || path.endsWith("/")) {
                    encodedPath.append("/");
                }
            }

            return encodedPath.toString();
        } catch (Exception e) {
            // UTF-8 总是支持的，这里只是处理异常
            return path;
        }
    }

}
