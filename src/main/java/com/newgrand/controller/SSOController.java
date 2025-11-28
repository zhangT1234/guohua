package com.newgrand.controller;

import cn.hutool.http.webservice.SoapClient;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.newgrand.domain.model.Fg3User;
import com.newgrand.domain.model.Secuser;
import com.newgrand.mapper.Fg3UserMapper;
import com.newgrand.mapper.SecuserMapper;
import com.newgrand.service.impl.UipLog;
import com.newgrand.utils.security.AESUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/11/17 9:14
 * @Description: TODO
 */
@Slf4j
@Api(tags = "i8单点登录接口")
@RestController
@RequestMapping("/SSO")
public class SSOController {

    @Value("${i8.url}")
    private String i8Url;

    @Value("${i8.databaseName}")
    private String databaseName;

    @Value("${oa.url}")
    private String oaUrl;

    @Value("${i6p.url}")
    private String i6pUrl;

    @Value("${i6p.esn}")
    private String i6pEsn;

    @Autowired
    private Fg3UserMapper fg3UserMapper;
    @Autowired
    private SecuserMapper secuserMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UipLog uipLog;
//    @Value("${i8.deskey}")
//    private String i8Deskey;

    @RequestMapping("/wfmsg")
    public void Wfmsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String logid = request.getParameter("logid");
        String database = request.getParameter("database");
        String orgid = request.getParameter("orgid");
        String openUrl = request.getParameter("openUrl");
        String urlTitle = request.getParameter("urlTitle");
        String openAlone = request.getParameter("openAlone");
        openUrl = URLEncoder.encode(openUrl, "UTF-8");

        QueryWrapper<Fg3User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userno", logid);
        Fg3User fg3User = fg3UserMapper.selectOne(queryWrapper);

        if (fg3User == null) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("用户不存在");
        } else {
            String userPwdStr = fg3User.getPwd();
            if (userPwdStr == null) {
                userPwdStr = "";
            } else {
                userPwdStr = URLEncoder.encode(userPwdStr, "utf-8");
            }
            String url = i8Url + "/Sup/NG3WebLogin/SignLogin?logid=" + logid + "&pwd=" + userPwdStr + "&database=" + database + "&orgid=" + orgid + "&openUrl=" + openUrl + "&urlTitle=" + urlTitle + "&openAlone=" + openAlone;
            response.sendRedirect(url);
        }
    }

    @RequestMapping("/login")
    public void Login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String logid = request.getParameter("logid");

        if (com.newgrand.utils.StringUtils.isBlank(logid)) {
            throw new RuntimeException("请携带logid!");
        }
        logid = AESUtil.aesDecrypt(logid);


        QueryWrapper<Fg3User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userno", logid);
        Fg3User fg3User = fg3UserMapper.selectOne(queryWrapper);

        if (fg3User == null) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("用户不存在");
        } else {
            if ("1".equals(fg3User.getMucpwd())) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("phid", fg3User.getPhid());
                updateWrapper.set("mucpwd", 0);
                fg3UserMapper.update(null, updateWrapper);
            }
            String userPwdStr = fg3User.getPwd();
            if (userPwdStr == null) {
                userPwdStr = "";
            } else {
                userPwdStr = URLEncoder.encode(userPwdStr, "utf-8");
            }
//            String url = i8Url + "/Sup/NG3WebLogin/SignLogin?logid=" + logid + "&pwd=" + userPwdStr + "&database=" + databaseName + "&orgid=&openUrl=&urlTitle=&openAlone=" + false;
            String loginfo = "logid=" + logid + "$@$pwd=" + userPwdStr + "$@$database=" + databaseName.replace("NG", "") + "$@$orgid=" + "$@$openUrl=" + "$@$urlTitle=" + "$@$openAlone=false";
            String url = i8Url + "/sup/NG3WebLogin/SSOLogin?loginfo=" + URLDecoder.decode(useWebservice(loginfo)).replace("$urlcompress$", "");
            response.sendRedirect(url);
        }
    }

    @RequestMapping("/logini6p")
    public void Logini6p(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            String logid = request.getParameter("logid");
            QueryWrapper<Secuser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("logid", logid);
            Secuser fg3User = secuserMapper.selectOne(queryWrapper);

            if (fg3User == null) {
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println("用户不存在");
                return;
            } else {
                String ocode = fg3User.getLastloginorg();
                String pwd = fg3User.getPwd();
                String loginfo = "dbserver=$@$accout=0001$@$ocode=" + ocode + "$@$logid=" + logid + "$@$pwd=$@$language=zh-cn$@$hidmainform=0$@$funtionurl=$@$epwd=" + pwd;

                JSONObject ssoJO = new JSONObject();
                ssoJO.put("ssoUrl", i6pUrl);
                ssoJO.put("LogInInfo", loginfo);
                ssoJO.put("DeptInfo", i6pEsn);
                ssoJO.put("Production", "i6P");
                String ssoStr = ssoJO.toJSONString();
                String base64 = Base64.getEncoder().encodeToString(ssoStr.getBytes());
                String redirectUrl = "NGIE://" + base64;
                response.sendRedirect(redirectUrl);
            }
        } catch (Exception ex) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("接口异常:" + ex.getMessage());
            return;
        }


    }

    /**
     * 5.1登录串编码
     * @param info
     * @return
     */
    public String useWebservice(String info) {
        //创建soap客户端
        SoapClient soapclient = SoapClient.create(i8Url + "/i6Service/NGExternalService.asmx")
                // 设置请求头
                .header("SOAPAction", "http://tempuri.org/INGExternalService/SSOUrlCompress")
                //设置调用方法名称以及命名空间
                .setMethod("SSOUrlCompress", "http://tempuri.org/")
                .setParam("input", info, false);
        String result = soapclient.send(false);
        System.out.println(result);
        //截取返回
        int start = result.indexOf("<SSOUrlCompressResult>");
        int end = result.indexOf("</SSOUrlCompressResult>");
        if (start == -1 || end == -1) {
            throw new RuntimeException("请求WS文件没有返回文件");
        } else {
            return result.substring(start + ("<SSOUrlCompressResult>").length(), end);
        }
    }

    /**
     * 单点登录到合同
     */
    @GetMapping("/contract")
    public void contract(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
          1.校验code
         */
        String logid = request.getParameter("logid");

        String id = request.getParameter("id");

        if (com.newgrand.utils.StringUtils.isBlank(logid)) {
            throw new RuntimeException("请携带logid!");
        }
        logid = AESUtil.aesDecrypt(logid);

        QueryWrapper<Fg3User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userno", logid);
        Fg3User fg3User = fg3UserMapper.selectOne(queryWrapper);

        if (fg3User == null) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("用户不存在");
        } else {
            if ("1".equals(fg3User.getMucpwd())) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("phid", fg3User.getPhid());
                updateWrapper.set("mucpwd", 0);
                fg3UserMapper.update(null, updateWrapper);
            }
            String userPwdStr = fg3User.getPwd();
            if (userPwdStr == null) {
                userPwdStr = "";
            } else {
                userPwdStr = URLEncoder.encode(userPwdStr, "utf-8");
            }

            List<Map<String, Object>> data = jdbcTemplate.queryForList("select pcm3_cnt_m.cnt_type,pcm3_cnt_type.cnt_mode from pcm3_cnt_m left join pcm3_cnt_type on pcm3_cnt_m.cnt_type = pcm3_cnt_type.phid where pcm3_cnt_m.phid = " + id);
            if(data.isEmpty()) {
                PrintWriter out = response.getWriter();
                out.println("找不到合同");
                return;
            }
            Short model = (Short) data.get(0).get("cnt_mode");
            Long cntType = (Long) data.get(0).get("cnt_type");

            /*
                2.网页端跳转的url
             */
            String openUrl = "/PMS/PCM/CntM/CntMEdit?otype=view&model=" + model + "&id=" + id + "&cnttype=" + cntType + "&AppTitle=合同查看";
            String loginfo = "logid=" + fg3User.getUserno() + "$@$pwd=" + userPwdStr + "$@$database=" +
                    databaseName.replace("NG", "") + "$@$orgid=" + fg3User.getCurOrgid() +
                    "$@$openUrl=" + openUrl + "$@$urlTitle=" + "$@$openAlone=false";
            log.info("拼接成的loginfo为: {}", loginfo);
            String url = i8Url + "/sup/NG3WebLogin/SSOLogin?loginfo=" +
                    URLDecoder.decode(useWebservice(loginfo)).replace("$urlcompress$", "");
            log.info("拼接成的url为: {}", url);

            response.sendRedirect(url);
        }
    }

    /**
     * 单点登录到收票
     */
    @GetMapping("/AttachView")
    public void attachView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
          1.校验code
         */
        String logid = request.getParameter("logid");
        QueryWrapper<Fg3User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userno", logid);
        Fg3User fg3User = fg3UserMapper.selectOne(queryWrapper);

        if (fg3User == null) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("用户不存在");
        } else {
            if ("1".equals(fg3User.getMucpwd())) {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("phid", fg3User.getPhid());
                updateWrapper.set("mucpwd", 0);
                fg3UserMapper.update(null, updateWrapper);
            }
            String userPwdStr = fg3User.getPwd();
            if (userPwdStr == null) {
                userPwdStr = "";
            } else {
                userPwdStr = URLEncoder.encode(userPwdStr, "utf-8");
            }

            /*
                2.网页端跳转的url
             */
            String openUrl = "I8/ITM/INV/InvoBill/InvoBillList?showType=439";
            String loginfo = "logid=" + fg3User.getUserno() + "$@$pwd=" + userPwdStr + "$@$database=" +
                    databaseName.replace("NG", "") + "$@$orgid=" + fg3User.getCurOrgid() +
                    "$@$openUrl=" + openUrl + "$@$urlTitle=" + "$@$openAlone=false";
            log.info("拼接成的loginfo为: {}", loginfo);
            String url = i8Url + "/sup/NG3WebLogin/SSOLogin?loginfo=" +
                    URLDecoder.decode(useWebservice(loginfo)).replace("$urlcompress$", "");
            log.info("拼接成的url为: {}", url);

            /*
                3.客户端跳转的url
                ngIeLoginfo为跳转的登录串
                    -- dbserver: 数据库服务器名
                    -- accout: 数据库名(去掉ng前缀, 比如数据库为ng0001, 则传0001)
                    -- ocode: 组织号
                    -- logid: 操作员号
                    -- pwd: 密码,默认为空
                    -- language: 语言(默认是：zh - cn)
                    -- hidmainform: 是否隐藏主窗口(1表示要隐藏，0表示不隐藏)
                    -- otherparam: 其他参数，用于一些特殊用途(基本不用管)
                    -- funtionurl : 功能菜单的url
             */
            String functionUrl = "I8/ITM/INV/InvoBill/InvoBillList";
            /*
                1.account取I8左下角的账套
                2.ocode取左下角的组织
                3.functionUrl指的是你想要跳转到的页面对应的链接
                4.epwd取操作员密码
             */
            String ngIeLoginfo = "dbserver=$@$accout=0001$@$ocode=DLJS$@$logid=" + fg3User.getUserno() +
                    "$@$pwd=$@$language=zh-cn$@$hidmainform=0$@$funtionurl=" + functionUrl + "$@$epwd=" +
                    fg3User.getPwd();

            JSONObject body = new JSONObject();
            //i8地址
            body.put("ssoUrl", i8Url);
            //单点登录串
            body.put("LogInInfo", ngIeLoginfo);
            //加密狗号(见于产品的 关于->序列号)
            body.put("DeptInfo", "760480");
            //注意要NGIE开头
            String NGIEUrl = "NGIE://" + Base64.getEncoder().encodeToString(JSONObject.toJSONString(body).getBytes(StandardCharsets.UTF_8));
            log.info("编码前的ngieUrl: {}", JSONObject.toJSONString(body));
            log.info("拼接成的NGIEUrl: {}", NGIEUrl);
            response.sendRedirect(NGIEUrl);
        }
    }
}
