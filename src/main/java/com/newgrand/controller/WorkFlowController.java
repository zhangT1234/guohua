package com.newgrand.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newgrand.domain.model.Fg3User;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.impl.WorkFlowService;
import com.newgrand.mapper.Fg3UserMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/19 20:07
 * @Description: TODO
 */
@Slf4j
@Api(tags = "i8工作流待办推送BIP系统")
@RestController
@RequestMapping("/TaskMsg")
public class WorkFlowController {
    @Value("${i8.url}")
    private String i8Url;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private Fg3UserMapper fg3UserMapper;

    @RequestMapping(value = "/Send", method = RequestMethod.POST)
    public @ResponseBody
    I8ReturnModel Send(@RequestBody String jsonStr) throws Exception {
        return workFlowService.Send(jsonStr);
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
     * 用于统一待办点击进去中转
     *
     * @param request
     * @param response
     * @throws IOException
     */
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
            //dbLog.info("wfmsg", "单点返回url：" + url);
            response.sendRedirect(url);
        }
    }


}
