package com.newgrand.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.domain.model.Fg3User;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.SecDevTaskMsg;
import com.newgrand.utils.i8util.HttpHelper;
import com.newgrand.utils.i8util.PropHelper;
import com.newgrand.mapper.ActHiTaskinstMapper;
import com.newgrand.mapper.Fg3UserMapper;
import com.newgrand.mapper.SecDevTaskMsgMapper;
import com.newgrand.utils.i8util.StringHelper;
import com.newgrand.utils.uuid.GetNewPhidUtils;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/31 10:59
 * @Description: TODO
 */
@Service
public class WorkFlowWeaverService {


    @Value("${i8.databaseName}")
    private String i8DbName;

    @Value("${oa.registerCode}")
    private String registerCode;


    @Value("${i8.url}")
    private String i8Url;

    @Value("${i8.appurl}")
    private String i8App;
    @Value("${oa.url}")
    private String oaUrl;

    @Autowired
    private PropHelper env;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UipLog dbLog;

    @Autowired
    private HttpHelper httpHelper;


    @Autowired
    private SecDevTaskMsgMapper secDevTaskMsgMapper;

    @Autowired
    private Fg3UserMapper fg3UserMapper;
    @Autowired
    private ActHiTaskinstMapper actHiTaskinstMapper;


    public I8ReturnModel Send(String jsonStr) throws Exception {
        I8ReturnModel rv = new I8ReturnModel();
        try {
            dbLog.info("taskmsg", "产品推送", jsonStr);
            //先将接收到的数据插入中间表 AddNew 状态的数据
            JSONArray workflowJA = JSON.parseArray(jsonStr);
            int wfCount = workflowJA.size();
            for (int i = 0; i < wfCount; i++) {
                JSONObject wf = workflowJA.getJSONObject(i);
                String taskid = wf.getString("taskid");
                String title = wf.getString("title");
                String url = wf.getString("url");
                String dbName = wf.getString("dbName");
                if (!i8DbName.equals(dbName)) continue; //非当前启用账套
                String currentUser = wf.getString("currentUser");
                String status = wf.getString("status");
                if (!"addNew".equals(status)) continue; //只记录AddNew数据
                JSONArray usersJA = wf.getJSONArray("users");
                int userCount = usersJA.size();
                for (int m = 0; m < userCount; m++) {
                    String userid = usersJA.getString(m);
                    String phid = GetNewPhidUtils.getPhid();
                    secDevTaskMsgMapper.delete(
                            new QueryWrapper<SecDevTaskMsg>()
                                    .lambda().
                                    eq(SecDevTaskMsg::getTaskid, taskid)
                                    .eq(SecDevTaskMsg::getUserid, userid)
                                    .eq(SecDevTaskMsg::getPushadd, '0')
                                    .eq(SecDevTaskMsg::getPushdone, '0')
                                    .eq(SecDevTaskMsg::getPushdel, '0'));
                    //记录数据库
                    SecDevTaskMsg secDevTaskMsg = SecDevTaskMsg.builder()
                            .phid(phid).taskid(taskid).userid(userid).url(url).dbname(dbName).status("addNew")
                            .build();
                    secDevTaskMsgMapper.insert(secDevTaskMsg);
                }
            }

            for (int i = 0; i < wfCount; i++) {
                JSONObject wf = workflowJA.getJSONObject(i);
                String taskid = wf.getString("taskid");
                String title = wf.getString("title");
                String url = wf.getString("url");
                String dbName = wf.getString("dbName");
                if (!i8DbName.equals(dbName)) continue; //非当前启用账套
                String currentUser = wf.getString("currentUser");
                String status = wf.getString("status");
                JSONArray usersJA = wf.getJSONArray("users");
                int userCount = usersJA.size();
                SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if ("addNew".equals(status)) {
                    //新增待办
                    List<Map<String, Object>> wfInfo = jdbcTemplate.queryForList(
                            "SELECT p.id_ insid,t.NAME_ as nodename,t.START_TIME_ receivedatetime,p.START_TIME_ createdatetime,\n" +
                                    "u1.phid as creatorphid,u1.userno startuserno,pd.NAME_ pdname,p.BUSINESS_KEY_ keyname,anbi.bizname as workflowname\n" +
                                    "FROM ACT_HI_TASKINST t\n" +
                                    "JOIN ACT_HI_PROCINST p ON t.PROC_INST_ID_ =p.ID_\n" +
                                    "JOIN fg3_user u1 ON u1.phid =p.START_USER_ID_ \n" +
                                    "JOIN ACT_RE_PROCDEF pd ON pd.ID_ =p.PROC_DEF_ID_\n" +
                                    "Join ACT_NG_BIZCOMPONENT anbcp on anbcp.componentid=t.form_resource_key_\n" +
                                    "Join act_ng_bizinfo anbi on anbi.Bizid=anbcp.bizid\n" +
                                    "WHERE t.ID_=?", taskid);
                    if (wfInfo != null && wfInfo.size() > 0) {
                        Map<String, Object> workFlow = wfInfo.get(0);
                        String insid = StringHelper.nullToEmpty(workFlow.get("insid"));//流程id
                        String workflowname = StringHelper.nullToEmpty(workFlow.get("workflowname"));//流程类型名称
                        String nodename = StringHelper.nullToEmpty(workFlow.get("nodename"));//步骤名称(节点名称)
                        String creatorphid = StringHelper.nullToEmpty(workFlow.get("creatorphid"));//创建人phid
                        String createdatetime = bartDateFormat.format(wfInfo.get(0).get("createdatetime"));
                        String receivedatetime = bartDateFormat.format(wfInfo.get(0).get("receivedatetime"));
                        for (int m = 0; m < userCount; m++) {
                            String userid = usersJA.getString(m);//接收人phid
                            JSONObject root_push = new JSONObject();
                            Fg3User receiverInfo = fg3UserMapper.selectById(userid);
                            Fg3User creatorInfo = fg3UserMapper.selectById(creatorphid);
                            String receiver = receiverInfo.getUserno();
                            String creator = creatorInfo.getUserno();

                            root_push.put("syscode", registerCode);//注册系统编码
                            root_push.put("flowid", taskid);//
                            root_push.put("requestname", title);//待办标题
                            root_push.put("workflowname", workflowname);//
                            root_push.put("nodename", nodename);//
                            root_push.put("isremark", "0");//流程处理状态 0：待办 2：已办 4：办结
                            root_push.put("viewtype", "0");//流程查看状态 0：未读 1：已读;
                            root_push.put("creator", creator);//
                            root_push.put("createdatetime", createdatetime);//
                            root_push.put("receiver", receiver);//
                            root_push.put("receivedatetime", receivedatetime);//
                            root_push.put("receivets", System.currentTimeMillis() + "");//
                            //组装PCurl链接地址
                            String pwd = StringHelper.nullToEmpty(receiverInfo.getPwd());
                            String org = receiverInfo.getLastloginorg();
                            if (url.startsWith("/")) {
                                url = url.substring(1);
                            }//统一去掉第一个斜杠
                            String base64openUrl = URLEncoder.encode(url, "UTF-8");
                            //Sup/NG3WebLogin/SignLogin
                            String pcurl = i8Url + "/Huangma/TaskMsg/wfmsg?logid=" + receiver + "&pwd=" + URLEncoder.encode(pwd, "UTF-8") + "&database=" + i8DbName + "&orgid=" + org + "&openUrl=" + base64openUrl + "&urlTitle=" + URLEncoder.encode(title, "UTF-8") + "&openAlone=true";

                            root_push.put("pcurl", pcurl);//PC穿透地址
                            //组装app链接地址
                            JSONObject pushdata = new JSONObject();
                            JSONObject pushdatadata = new JSONObject();
                            pushdatadata.put("piid", insid);
                            pushdatadata.put("taskinstid", taskid);
                            pushdata.put("data", pushdatadata);
                            String pushdd = URLEncoder.encode(pushdata.toJSONString(), "UTF-8");
                            String appurl = i8App + "&requestType=MEA&userid=" + receiver + "_" + i8DbName.replace("NG", "") + "&pushdata=" + pushdd;
                            root_push.put("appurl", appurl);//H5穿透地址

                            StringEntity entity = new StringEntity(root_push.toJSONString(), Charset.forName("UTF-8"));
                            entity.setContentType("application/json");
                            Header[] headers = new Header[1];
                            Header header = new BasicHeader("Content-Type", "application/json");
                            headers[0] = header;
                            String addNewUrl = env.Get("oa.url") + "/rest/ofs/ReceiveRequestInfoByJson";
                            String rtnMsg = httpHelper.Post(addNewUrl, entity, headers);
                            dbLog.info("addNew", "推送新增待办", addNewUrl + "推送:" + root_push.toJSONString() + "接口返回:" + rtnMsg);
                            //更新新增待办推送标识和时间
                            JSONObject joRtn = JSONObject.parseObject(rtnMsg);
                            if ("1".equals(joRtn.getString("operResult"))) {
                                Date nowTime = new Date();
                                UpdateWrapper updateWrapper = new UpdateWrapper();
                                updateWrapper.eq("taskid", taskid);
                                updateWrapper.eq("userid", userid);
                                updateWrapper.set("pushadd", "1");
                                updateWrapper.set("pushaddtime", bartDateFormat.format(nowTime));
                                secDevTaskMsgMapper.update(null, updateWrapper);
                            } else {
                                if (joRtn.getString("message").contains("不存在")) {
                                    Date nowTime = new Date();
                                    UpdateWrapper updateWrapper = new UpdateWrapper();
                                    updateWrapper.eq("taskid", taskid);
                                    updateWrapper.eq("userid", userid);
                                    updateWrapper.set("pushadd", "1");
                                    updateWrapper.set("pushaddtime", bartDateFormat.format(nowTime));
                                    secDevTaskMsgMapper.update(null, updateWrapper);
                                }
                            }
                        }
                    }
                } else if ("done".equals(status)) {
                    //已办
                    List<Map<String, Object>> wfInfo = jdbcTemplate.queryForList(
                            "SELECT p.id_ insid,t.NAME_ as nodename,t.START_TIME_ receivedatetime,p.START_TIME_ createdatetime,\n" +
                                    "u1.phid as creatorphid,u1.userno startuserno,pd.NAME_ pdname,p.BUSINESS_KEY_ keyname,anbi.bizname as workflowname\n" +
                                    "FROM ACT_HI_TASKINST t\n" +
                                    "JOIN ACT_HI_PROCINST p ON t.PROC_INST_ID_ =p.ID_\n" +
                                    "JOIN fg3_user u1 ON u1.phid =p.START_USER_ID_ \n" +
                                    "JOIN ACT_RE_PROCDEF pd ON pd.ID_ =p.PROC_DEF_ID_\n" +
                                    "Join ACT_NG_BIZCOMPONENT anbcp on anbcp.componentid=t.form_resource_key_\n" +
                                    "Join act_ng_bizinfo anbi on anbi.Bizid=anbcp.bizid\n" +
                                    "WHERE t.ID_=?", taskid);
                    if (wfInfo != null && wfInfo.size() > 0) {
                        Map<String, Object> workFlow = wfInfo.get(0);
                        String insid = StringHelper.nullToEmpty(workFlow.get("insid"));//流程id
                        String workflowname = StringHelper.nullToEmpty(workFlow.get("workflowname"));//流程类型名称
                        String nodename = StringHelper.nullToEmpty(workFlow.get("nodename"));//步骤名称(节点名称)
                        String creatorphid = StringHelper.nullToEmpty(workFlow.get("creatorphid"));//创建人phid
                        String createdatetime = bartDateFormat.format(wfInfo.get(0).get("createdatetime"));
                        String receivedatetime = bartDateFormat.format(wfInfo.get(0).get("receivedatetime"));
                        for (int m = 0; m < userCount; m++) {
                            String userid = usersJA.getString(m);//接收人phid
                            JSONObject root_push = new JSONObject();
                            Fg3User receiverInfo = fg3UserMapper.selectById(userid);
                            Fg3User creatorInfo = fg3UserMapper.selectById(creatorphid);
                            String receiver = receiverInfo.getUserno();
                            String creator = creatorInfo.getUserno();

                            root_push.put("syscode", registerCode);//注册系统编码
                            root_push.put("flowid", taskid);//
                            root_push.put("requestname", title);//待办标题
                            root_push.put("workflowname", workflowname);//
                            root_push.put("nodename", nodename);//
                            root_push.put("isremark", "2");//流程处理状态 0：待办 2：已办 4：办结
                            root_push.put("viewtype", "1");//流程查看状态 0：未读 1：已读;
                            root_push.put("creator", creator);//
                            root_push.put("createdatetime", createdatetime);//
                            root_push.put("receiver", receiver);//
                            root_push.put("receivedatetime", receivedatetime);//
                            root_push.put("receivets", System.currentTimeMillis() + "");//
                            //组装PCurl链接地址
                            String pwd = StringHelper.nullToEmpty(receiverInfo.getPwd());
                            String org = receiverInfo.getLastloginorg();
                            if (url.startsWith("/")) {
                                url = url.substring(1);
                            }//统一去掉第一个斜杠
                            String base64openUrl = URLEncoder.encode(url, "UTF-8");
                            //Sup/NG3WebLogin/SignLogin
                            String pcurl = i8Url + "/Huangma/TaskMsg/wfmsg?logid=" + receiver + "&pwd=" + URLEncoder.encode(pwd, "UTF-8") + "&database=" + i8DbName + "&orgid=" + org + "&openUrl=" + base64openUrl + "&urlTitle=" + URLEncoder.encode(title, "UTF-8") + "&openAlone=true";

                            root_push.put("pcurl", pcurl);//PC穿透地址
                            //组装app链接地址
                            JSONObject pushdata = new JSONObject();
                            JSONObject pushdatadata = new JSONObject();
                            pushdatadata.put("piid", insid);
                            pushdatadata.put("taskinstid", taskid);
                            pushdata.put("data", pushdatadata);
                            String pushdd = URLEncoder.encode(pushdata.toJSONString(), "UTF-8");
                            String appurl = i8App + "&requestType=MEA&userid=" + receiver + "_" + i8DbName.replace("NG", "") + "&pushdata=" + pushdd;
                            root_push.put("appurl", appurl);//H5穿透地址

                            StringEntity entity = new StringEntity(root_push.toJSONString(), Charset.forName("UTF-8"));
                            entity.setContentType("application/json");
                            Header[] headers = new Header[1];
                            Header header = new BasicHeader("Content-Type", "application/json");
                            headers[0] = header;
                            String addNewUrl = env.Get("oa.url") + "/rest/ofs/ReceiveRequestInfoByJson";
                            String rtnMsg = httpHelper.Post(addNewUrl, entity, headers);
                            dbLog.info("done", "推送待办已办", addNewUrl + "推送:" + root_push.toJSONString() + "接口返回:" + rtnMsg);
                            //更新新增待办推送标识和时间
                            JSONObject joRtn = JSONObject.parseObject(rtnMsg);
                            if ("1".equals(joRtn.getString("operResult"))) {
                                Date nowTime = new Date();
                                UpdateWrapper updateWrapper = new UpdateWrapper();
                                updateWrapper.eq("taskid", taskid);
                                updateWrapper.eq("userid", userid);
                                updateWrapper.set("pushdone", "1");
                                updateWrapper.set("pushdonetime", bartDateFormat.format(nowTime));
                                secDevTaskMsgMapper.update(null, updateWrapper);
                            } else {
                                if (joRtn.getString("message").contains("不存在")) {
                                    Date nowTime = new Date();
                                    UpdateWrapper updateWrapper = new UpdateWrapper();
                                    updateWrapper.eq("taskid", taskid);
                                    updateWrapper.eq("userid", userid);
                                    updateWrapper.set("pushdone", "1");
                                    updateWrapper.set("pushdonetime", bartDateFormat.format(nowTime));
                                    secDevTaskMsgMapper.update(null, updateWrapper);
                                }
                            }
                        }
                    }

                } else if ("delete".equals(status)) {
                    //删除待办
                    for (int m = 0; m < userCount; m++) {
                        String userid = usersJA.getString(m);//接收人phid
                        Fg3User receiverInfo = fg3UserMapper.selectById(userid);
                        String receiver = receiverInfo.getUserno();
                        JSONObject root_push = new JSONObject();
                        root_push.put("syscode", registerCode);//注册系统编码
                        root_push.put("flowid", taskid);//
                        root_push.put("userid", receiver);//

                        StringEntity entity = new StringEntity(root_push.toJSONString(), Charset.forName("UTF-8"));
                        entity.setContentType("application/json");
                        Header[] headers = new Header[1];
                        Header header = new BasicHeader("Content-Type", "application/json");
                        headers[0] = header;
                        String delUrl = env.Get("oa.url") + "/rest/ofs/deleteUserRequestInfoByJson";
                        String rtnMsg = httpHelper.Post(delUrl, entity, headers);
                        dbLog.info("delete", "推送删除待办", delUrl + "推送:" + root_push.toJSONString() + "接口返回:" + rtnMsg);
                        //更新新增待办推送标识和时间
                        JSONObject joRtn = JSONObject.parseObject(rtnMsg);
                        if ("1".equals(joRtn.getString("operResult"))) {
                            Date nowTime = new Date();
                            UpdateWrapper updateWrapper = new UpdateWrapper();
                            updateWrapper.eq("taskid", taskid);
                            updateWrapper.eq("userid", userid);
                            updateWrapper.set("pushdel", "1");
                            updateWrapper.set("pushdeltime", bartDateFormat.format(nowTime));
                            secDevTaskMsgMapper.update(null, updateWrapper);
                        } else {
                            if (joRtn.getString("message").contains("不存在")) {
                                Date nowTime = new Date();
                                UpdateWrapper updateWrapper = new UpdateWrapper();
                                updateWrapper.eq("taskid", taskid);
                                updateWrapper.eq("userid", userid);
                                updateWrapper.set("pushdel", "1");
                                updateWrapper.set("pushdeltime", bartDateFormat.format(nowTime));
                                secDevTaskMsgMapper.update(null, updateWrapper);
                            }
                        }
                    }
                }

            }

        } catch (Exception ex) {
            rv.setMessage("接口出现异常:" + ex.getMessage());
        }
        return rv;
    }

    public Map<String, String> GetPars(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf("&") > -1 && url.indexOf("=") > -1) {
            url = url.substring(url.indexOf("?") + 1);
            map = new HashMap<String, String>();
            String[] arrTemp = url.split("&");
            for (String str : arrTemp) {
                String[] qs = str.split("=");
                map.put(qs[0], qs[1]);
            }
        }
        return map;
    }

    /**
     * 定时推送新增待办接口
     */
    //@Scheduled(cron = "0 0/5 * * * ?")
    public void pushAddNew() {
        try {
            List<Map<String, Object>> wfInfo = jdbcTemplate.queryForList(
                    "SELECT i8task.taskid,i8task.userid,i8task.title,i8task.compurl,\n" +
                            "                                            i8task.bizurl,i8task.COMPONENTID,i8task.instcode,i8task.billid FROM (\n" +
                            "                            select art.ID_ taskid, COALESCE(art.ASSIGNEE_, ari.USER_ID_) userid,ahp.ID_ as instcode,\n" +
                            "                            ahp.BUSINESS_KEY_ AS title,anb.URL AS compurl,biz.URL AS bizurl,anp.pk1 as billid,anb.COMPONENTID\n" +
                            "                            from ACT_RU_TASK art\n" +
                            "                            left join ACT_RU_IDENTITYLINK ari on art.ID_ = ari.TASK_ID_\n" +
                            "                            LEFT JOIN act_hi_taskinst aha ON art.id_=aha.ID_\n" +
                            "                            LEFT JOIN ACT_NG_BIZCOMPONENT anb ON aha.FORM_RESOURCE_KEY_=anb.COMPONENTID\n" +
                            "                            LEFT JOIN act_ng_bizinfo biz ON biz.BIZID=anb.BIZID\n" +
                            "                            LEFT JOIN ACT_HI_PROCINST ahp ON ahp.ID_=aha.PROC_INST_ID_\n" +
                            "                            left join act_ng_processtrace anp on anp.instcode=ahp.ID_\n" +
                            "                            ) i8task\n" +
                            "                            WHERE NOT exists(\n" +
                            "                            SELECT phid FROM (SELECT phid,taskid,userid FROM sec_dev_task_msg WHERE pushadd='1') sdtm WHERE sdtm.taskid=i8task.taskid AND sdtm.userid=i8task.userid\n" +
                            "                            )");
            if (wfInfo != null && wfInfo.size() > 0) {
                int count = wfInfo.size();
                dbLog.info("pushAddNew", "推送新增待办异常", "满足条件条数:" + count);
                for (int i = 0; i < count; i++) {
                    Map<String, Object> workFlow = wfInfo.get(i);

                    JSONObject jo = new JSONObject();
                    JSONArray pushJA = new JSONArray();
                    String taskid = StringHelper.nullToEmpty(workFlow.get("taskid"));
                    String userid = StringHelper.nullToEmpty(workFlow.get("userid"));
                    if (StringUtils.isEmpty(userid)) continue;
                    String compurl = StringHelper.nullToEmpty(workFlow.get("compurl"));
                    String bizurl = StringHelper.nullToEmpty(workFlow.get("bizurl"));
                    String instcode = StringHelper.nullToEmpty(workFlow.get("instcode"));
                    String billid = StringHelper.nullToEmpty(workFlow.get("billid"));
                    if (StringUtils.isEmpty(billid)) continue;
                    String funUrl = "";
                    String url = StringUtils.isEmpty(compurl) ? bizurl : compurl;
                    url = url.startsWith("/") ? url.substring(1) : url;
                    String templateUrl = "wfpiid=" + instcode + "&wftaskid=" + taskid + "&wfotype=taskhandle&id=" + billid + "&otype=edit";
                    if (url.contains("?")) {
                        funUrl = url + "&";
                    } else {
                        funUrl = url + "?";
                    }
                    funUrl = funUrl + templateUrl;
                    jo.put("taskid", taskid);
                    jo.put("title", StringHelper.nullToEmpty(workFlow.get("title")));
                    jo.put("url", funUrl);
                    jo.put("dbName", i8DbName);
                    jo.put("currentUser", userid);
                    JSONArray users = new JSONArray();
                    users.add(userid);
                    jo.put("users", users);
                    jo.put("status", "addNew");
                    pushJA.add(jo);

                    Send(pushJA.toString());
                }
            }
        } catch (Exception ex) {
            dbLog.info("pushAddNew", "推送新增待办异常", ex.getMessage());
        }
    }


    /**
     * 定时推送已办或删除待办接口
     */
    //@Scheduled(cron = "0 0/5 * * * ?")
    public void pushDoneDel() {
        try {
            List<Map<String, Object>> wfInfo = jdbcTemplate.queryForList(
                    "SELECT sdtm.taskid,sdtm.userid,sdtm.url,ahp.ID_ as instcode,aht.ID_ AS i8taskid,\n" +
                            "                            aht.ASSIGNEE_ AS i8userid,\n" +
                            "                            aht.DELETE_REASON_ AS i8status,\n" +
                            "                            ahp.BUSINESS_KEY_ AS title,anb.URL AS compurl,biz.URL AS bizurl,anp.pk1 as billid \n" +
                            "                            FROM sec_dev_task_msg sdtm\n" +
                            "                            LEFT JOIN act_hi_taskinst aht ON aht.ID_=sdtm.taskid\n" +
                            "                            LEFT JOIN ACT_NG_BIZCOMPONENT anb ON aht.FORM_RESOURCE_KEY_=anb.COMPONENTID\n" +
                            "                            LEFT JOIN act_ng_bizinfo biz ON biz.BIZID=anb.BIZID\n" +
                            "                            LEFT JOIN ACT_HI_PROCINST ahp ON ahp.ID_=aht.PROC_INST_ID_\n" +
                            "                            left join act_ng_processtrace anp on anp.instcode=ahp.ID_\n" +
                            "                            WHERE (sdtm.pushadd='1' AND sdtm.pushdone<>'1' AND sdtm.pushdel<>'1')\n" +
                            "                            AND aht.DELETE_REASON_ IN ('completed','process terminated','deleted')");
            if (wfInfo != null && wfInfo.size() > 0) {
                int count = wfInfo.size();
                dbLog.info("pushDoneDel", "推送已办删除待办异常", "满足条件条数:" + count);
                for (int i = 0; i < count; i++) {
                    Map<String, Object> workFlow = wfInfo.get(i);

                    JSONObject jo = new JSONObject();
                    JSONArray pushJA = new JSONArray();
                    String taskid = StringHelper.nullToEmpty(workFlow.get("taskid"));
                    String userid = StringHelper.nullToEmpty(workFlow.get("userid"));
                    if (StringUtils.isEmpty(userid)) continue;
                    String compurl = StringHelper.nullToEmpty(workFlow.get("compurl"));
                    String bizurl = StringHelper.nullToEmpty(workFlow.get("bizurl"));
                    String instcode = StringHelper.nullToEmpty(workFlow.get("instcode"));
                    String billid = StringHelper.nullToEmpty(workFlow.get("billid"));
                    if (StringUtils.isEmpty(billid)) continue;
                    String funUrl = "";
                    String url = StringUtils.isEmpty(compurl) ? bizurl : compurl;
                    url = url.startsWith("/") ? url.substring(1) : url;
                    String templateUrl = "wfpiid=" + instcode + "&wftaskid=" + taskid + "&wfotype=taskhandle&id=" + billid + "&otype=view";
                    if (url.contains("?")) {
                        funUrl = url + "&";
                    } else {
                        funUrl = url + "?";
                    }
                    funUrl = funUrl + templateUrl;
                    jo.put("taskid", taskid);
                    jo.put("title", StringHelper.nullToEmpty(workFlow.get("title")));
                    jo.put("url", funUrl);
                    jo.put("dbName", i8DbName);
                    jo.put("currentUser", userid);
                    JSONArray users = new JSONArray();
                    users.add(userid);
                    jo.put("users", users);
                    String i8taskid = StringHelper.nullToEmpty(workFlow.get("i8taskid"));
                    String i8userid = StringHelper.nullToEmpty(workFlow.get("i8userid"));
                    String i8status = StringHelper.nullToEmpty(workFlow.get("i8status"));
                    String status = "delete";
                    if (i8userid == userid && i8status == "completed") {
                        status = "done";
                    }

                    jo.put("status", status);
                    pushJA.add(jo);

                    Send(pushJA.toString());
                }
            }
        } catch (Exception ex) {
            dbLog.info("pushDoneDel", "推送已办删除待办异常", ex.getMessage());
        }
    }


}
