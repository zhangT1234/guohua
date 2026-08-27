package com.newgrand.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.newgrand.domain.model.Fg3User;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.UserWechat;
import com.newgrand.mapper.Fg3UserMapper;
import com.newgrand.mapper.UserWechatMapper;
import com.newgrand.utils.CacheInfo;
import com.newgrand.utils.CacheUtil;
import com.newgrand.utils.HttpUtils;
import com.newgrand.utils.i8util.HttpHelper;
import com.newgrand.utils.i8util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/31 10:59
 * @Description: TODO
 */
@Service
@Slf4j
public class WorkFlowService {

    public static final String WECHAT_HOST = "https://api.weixin.qq.com";
    public static final String OFFICIAL_APPID = "wx35c484c5d6161b6d";
    public static final String OFFICIAL_SECRET = "b340f0e5991dbcd2bd72bc489c67ba54";
    public static final String OFFICIAL_GRANT_TYPE = "client_credential";
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String TEMPLATE_ID = "8qei9c1-onqk4REhVIeATldDdRqE_KlRU_p2wbqgnJo";
    public static final String GRANT_TYPE = "authorization_code";

    @Value("${i8.databaseName}")
    private String i8DbName;

    @Value("${i8.appurl}")
    private String i8App;

    @Autowired
    private Fg3UserMapper fg3UserMapper;

    @Autowired
    private UserWechatMapper userWechatMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public I8ReturnModel Send(String jsonStr) throws Exception {
        log.info("待办任务：" + jsonStr);
        I8ReturnModel rv = new I8ReturnModel();
        try {
            JSONArray workflowJA = JSON.parseArray(jsonStr);
            int wfCount = workflowJA.size();

            for (int i = 0; i < wfCount; i++) {
                JSONObject wf = workflowJA.getJSONObject(i);
                String taskid = wf.getString("taskid");
                String title = wf.getString("title");
                String status = wf.getString("status");
                JSONArray usersJA = wf.getJSONArray("users");
                int userCount = usersJA.size();
                if ("addNew".equals(status)) {
                    //新增待办
                    List<Map<String, Object>> wfInfo = jdbcTemplate.queryForList(
                            "SELECT p.id_ insid,t.NAME_ as nodename,t.START_TIME_ receivedatetime,p.START_TIME_ createdatetime," +
                                    "u1.phid as creatorphid,u1.userno startuserno,pd.NAME_ pdname,p.BUSINESS_KEY_ keyname,anbi.bizname as workflowname" +
                                    "FROM ACT_HI_TASKINST t" +
                                    "JOIN ACT_HI_PROCINST p ON t.PROC_INST_ID_ =p.ID_" +
                                    "JOIN fg3_user u1 ON u1.phid =p.START_USER_ID_ " +
                                    "JOIN ACT_RE_PROCDEF pd ON pd.ID_ =p.PROC_DEF_ID_" +
                                    "Join ACT_NG_BIZCOMPONENT anbcp on anbcp.componentid=t.form_resource_key_" +
                                    "Join act_ng_bizinfo anbi on anbi.Bizid=anbcp.bizid" +
                                    "WHERE t.ID_=?", taskid);
                    if (wfInfo != null && wfInfo.size() > 0) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]");
                        DateTimeFormatter bartDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        Map<String, Object> workFlow = wfInfo.get(0);
                        String insid = StringHelper.nullToEmpty(workFlow.get("insid"));//流程id
                        LocalDateTime dateTime1 = LocalDateTime.parse(wfInfo.get(0).get("createdatetime").toString(), formatter);
                        String createdatetime = dateTime1.format(bartDateFormat);
                        String creatorphid = StringHelper.nullToEmpty(workFlow.get("creatorphid"));//创建人phid
                        Fg3User creatorInfo = fg3UserMapper.selectById(creatorphid);

                        if(title.length() >= 19) {
                            title = title.substring(0, 19);
                        }
                        String username = creatorInfo.getUsername();
                        if(username.length() >= 19) {
                            username = username.substring(0, 19);
                        }
                        if(createdatetime.length() >= 19) {
                            createdatetime = createdatetime.substring(0, 19);
                        }
                        for (int m = 0; m < userCount; m++) {
                            String userid = usersJA.getString(m);//接收人phid
                            Fg3User receiverInfo = fg3UserMapper.selectById(userid);
                            if (receiverInfo == null || StringUtils.isEmpty(receiverInfo.getUserno())) {
                                continue;
                            }
                            String receiver = receiverInfo.getUserno();
                            UserWechat userWechat = userWechatMapper.selectOne(
                                    new QueryWrapper<UserWechat>().eq("username", receiver));
                            if (userWechat == null || StringUtils.isEmpty(userWechat.getOpenid())) {
                                log.info("未绑定openid, receiver=" + receiver);
                                continue;
                            }
                            //组装app链接地址
                            JSONObject pushdata = new JSONObject();
                            JSONObject pushdatadata = new JSONObject();
                            pushdatadata.put("piid", insid);
                            pushdatadata.put("taskinstid", taskid);
                            pushdata.put("data", pushdatadata);
                            String pushdd = URLEncoder.encode(pushdata.toJSONString(), "UTF-8");
                            String url = i8App + "&module=MEA&userName=" + receiver + "&accountNo=" + i8DbName.replace("ng", "") + "&pushdata=" + pushdd;
                            JSONObject bodyJSON = new JSONObject();
                            JSONObject dataJSON = new JSONObject();
                            bodyJSON.put("touser", userWechat.getOpenid());
                            bodyJSON.put("template_id", TEMPLATE_ID);
                            dataJSON.put("thing15", JSONObject.parseObject("{\"value\":\"" + title + "\",\"color\":\"#173177\"}"));
                            dataJSON.put("thing23", JSONObject.parseObject("{\"value\":\"" + username + "\",\"color\":\"#173177\"}"));
                            dataJSON.put("thing32", JSONObject.parseObject("{\"value\":\"" + createdatetime + "\",\"color\":\"#173177\"}"));
                            bodyJSON.put("url", url);
                            bodyJSON.put("data", dataJSON);
                            sendMsg(bodyJSON);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("接口出现异常:" + ex.getMessage());
            rv.setMessage("接口出现异常:" + ex.getMessage());
        }
        return rv;
    }

    public String getOpenid(String code) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String uri = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + OFFICIAL_APPID + "&secret=" + OFFICIAL_SECRET
                + "&grant_type=" + GRANT_TYPE + "&code=" + code;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        System.out.println(response);
        JSONObject result = JSONObject.parseObject(response.getBody());
        if (result.containsKey("errcode")) {
            log.info("微信错误: " + result.getString("errmsg"));
            return null;
        }

        return result.getString("openid");
    }

    public String sendMsg(JSONObject bodyJSON) {
        final String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("access_token", getToken());
        try {
            HttpResponse response = HttpUtils.doPost(WECHAT_HOST, "/cgi-bin/message/template/send",
                    method, headers, querys, JSONObject.toJSONString(bodyJSON));
            String resultStr = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject result = JSONObject.parseObject(resultStr);
            int resultCode = result.getIntValue("errcode");
            if(resultCode != 0) {
                log.info(resultStr);
                if(resultCode == 40001 || resultCode == 42001) {
                    CacheUtil.remove(ACCESS_TOKEN_KEY);
                    return sendMsg(bodyJSON);
                }
            }
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized String getToken() {
        CacheInfo cached = CacheUtil.get(ACCESS_TOKEN_KEY);
        if (cached != null && cached.getInfo() != null) {
            return cached.getInfo();
        }
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("grant_type", OFFICIAL_GRANT_TYPE);
        querys.put("appid", OFFICIAL_APPID);
        querys.put("secret", OFFICIAL_SECRET);
        try {
            HttpResponse response = HttpUtils.doGet(WECHAT_HOST, "/cgi-bin/token", headers, querys);
            String resultStr = EntityUtils.toString(response.getEntity());
            JSONObject resultJSON = JSONObject.parseObject(resultStr);
            if(resultJSON.containsKey("access_token")) {
                String accessToken = resultJSON.getString("access_token");
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.setInfo(accessToken);
                CacheUtil.put(ACCESS_TOKEN_KEY, cacheInfo);
                return accessToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
