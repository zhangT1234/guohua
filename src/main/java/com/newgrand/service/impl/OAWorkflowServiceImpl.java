package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.newgrand.config.OAProperties;
import com.newgrand.service.OAAuthService;
import com.newgrand.service.OAWorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * OA 工作流服务实现
 *
 * @author guohua
 */
@Slf4j
@Service
public class OAWorkflowServiceImpl implements OAWorkflowService {

    @Resource
    private OAProperties oaProperties;

    @Resource
    private OAAuthService oaAuthService;

    @Override
    public String createWorkflow(String requestName, String workflowId, Map<String, Object> mainData, List<Map<String, Object>> detailData, String userId) {
        // 1. 获取 Token
        String token = oaAuthService.getToken(userId);

        // 2. 加密 userId（使用 spk）
        String encryptedUserId = oaAuthService.encryptUserId(userId);

        // 3. 转换 mainData 从 Map 到数组格式
        JSONArray mainDataArray = new JSONArray();
        for (Map.Entry<String, Object> entry : mainData.entrySet()) {
            JSONObject field = new JSONObject();
            field.put("fieldName", entry.getKey());
            field.put("fieldValue", entry.getValue());
            mainDataArray.add(field);
        }

        JSONArray detailDataArray = null;
        if (CollectionUtil.isNotEmpty(detailData)) {
            // 创建第一个对象
            detailDataArray = new JSONArray();
            JSONObject mainObject = new JSONObject();
            //mainObject.put("tableDBName", "formtable_main_244_dt1"); //正式
            //mainObject.put("tableDBName", "formtable_main_688_dt1"); //正式
           // mainObject.put("tableDBName", "formtable_main_219_dt1"); //测试
            mainObject.put("tableDBName", "formtable_main_690_dt1");
            // 创建workflowRequestTableRecords数组
            JSONArray workflowRequestTableRecords = new JSONArray();
            for (Map<String, Object> detailMap : detailData) {
                JSONObject record = new JSONObject();
                record.put("recordOrder", 0);
                JSONArray fields = new JSONArray();
                for (Map.Entry<String, Object> entry : detailMap.entrySet()) {
                    JSONObject field = new JSONObject();
                    field.put("fieldName", entry.getKey());
                    field.put("fieldValue", entry.getValue());
                    fields.put(field);
                }
                record.put("workflowRequestTableFields", fields);
                workflowRequestTableRecords.put(record);
            }
            // 将workflowRequestTableRecords放入mainObject
            mainObject.put("detailData_tableName", "");
            mainObject.put("workflowRequestTableRecords", workflowRequestTableRecords);
            // 将mainObject放入最外层的jsonArray
            detailDataArray.put(mainObject);
        }

        // 4. 调用创建工作流接口（使用 form 格式）
        String url = oaProperties.getBaseUrl() + "/api/workflow/paService/doCreateRequest";

        log.info("[OA] 创建工作流，requestName: {}, workflowId: {}, mainData: {}, detailData: {}",
                requestName, workflowId, mainDataArray.toString(),
                detailDataArray != null ? detailDataArray.toString() : "null");

        try {
            HttpRequest request = HttpRequest.post(url)
                    .header("token", token)
                    .header("appid", oaProperties.getAppid())
                    .header("userid", encryptedUserId)
                    .form("requestName", requestName)
                    .form("workflowId", workflowId)
                    .form("mainData", mainDataArray.toString())
                    .timeout(60000); // 超时时间设置为 60 秒（明细表数据可能较大，需要更长处理时间）

            // 如果有 detailData，添加到请求中
            if (detailDataArray != null) {
                request.form("detailData", detailDataArray.toString());
            }

            HttpResponse response = request.execute();
            String responseBody = response.body();
            log.info("[OA] 创建工作流响应: {}", responseBody);

            if (StrUtil.isEmpty(responseBody)) {
                throw new Exception("OA 系统响应为空");
            }
            return responseBody;
        }  catch (Exception e) {
            log.info("[OA] 创建工作流失败", e);
            return null;
        }
    }

}

