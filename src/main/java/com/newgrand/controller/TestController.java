package com.newgrand.controller;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.mapper.UIPCommonMapper;
import com.newgrand.service.EnterpriseService;
import com.newgrand.utils.i8util.HttpHelper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhanglixin
 * @Data: 2022/12/6 13:35
 * @Description: TODO
 */
@Slf4j
@Api(tags = "测试接口")
@RestController
@RequestMapping("/Test")
public class TestController {

    @Autowired
    private UIPCommonMapper uipCommonMapper;

    @Autowired
    private HttpHelper httpHelper;

    @Autowired
    private EnterpriseService  enterpriseService;

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    @ResponseBody
    public Object GetData() throws Exception {
        try {
            log.info("测试接口返回成功");
            return "接口返回成功";
        } catch (Exception ex) {
        }
        return null;
    }

    @RequestMapping(value = "/getData2", method = RequestMethod.GET)
    @ResponseBody
    public Object GetData2() throws Exception {
        try {
            List<Map<String, Object>> listParent = uipCommonMapper.dynamicSql("select * from fg_orglist");
            return listParent;
        } catch (Exception ex) {
        }
        return null;
    }

    @RequestMapping(value = "/getData3", method = RequestMethod.GET)
    @ResponseBody
    public Object getData3() throws Exception {
        try {
            String url = "http://10.10.10.151:8888/rest/ofs/ReceiveRequestInfoByJson";
            String data = "{\"requestname\":\"劳务分包合同审批测试劳务合同66 9999\",\"creator\":\"9999\",\"receiver\":\"test123\",\"workflowname\":\"劳务分包合同信息\",\"pcurl\":\"BFutwb16YIpUMt_AuuXq2dnflYgTLvGhqDWaZQW0tYhQTpLgeMI5OcEvi3y1wQw94vTCt4DYQXTITkR9ViMjjc0xOmJmXI2S7o68bOFdtJMOBccRAPzQb647GO09pFe8_ChHgwoovD-OeL1FXS5-ZpYZv4_KCOPYXGYyQOibPCRNLvYPIqZzUP-xB9sjfK6RDtYsABNpBD2HEwEnTaHHa8TmJvQtlrTBmKohJ10TAeVE-Oew5S_vwNDGAmid-NPfVNIoBExuNbtAlDqNdvdCHFydDmk0zww2wPDzB_RWOn_RtMl84PLGhx8rrAHG5_c3OXyS0tILCxssHCLzkeSXi4x9c9sgapJW4jdsCxAU5PZFrVazvm7KIa-xBnknr4qPn8EMcZhr5gTnseA02BAzOoXYUwTVuufa0FC-WCodvaN28GYyNc5xh31oQrtiU8LXopDpYdaZ0tMxv0zS3XKTXSoD8IWylR-4FMZsXEqWB5PsH5KkVLoqc8JpzP993m1A\",\"viewtype\":\"0\",\"appurl\":\"-jaE30pR77ll8beCmI31lamN0IkDuLzsIbM-plKh_FRz6ivN-BzAWg-V5tZSHFoDOFZo2a5F41dvivevLOWVLdj9nMLpAzD_5PRci_WMH8_sdUEu2btKmApDLR2dDqUH00JDQfvlxzUsz9oAyWhwSgOo3nK_6vXJZxz_X6AhM1HHlFiy9owpIX92Z2w6uYPe3l08YXYWudzDg3LzaWUzw1ipcOwjuDuj3KI3pL7bJkL3Maej_rpM82gzDYtzS5TJCr2ES5aLgPdEn_GQd9am253orbB2YXNPpQ3Kv0BQ-ScIvGe9o9xHfrP71OvrwAMK\",\"syscode\":\"\",\"nodename\":\"审核\",\"receivets\":\"1766121071047\",\"receivedatetime\":\"2025-12-19 13:11:10\",\"isremark\":\"0\",\"createdatetime\":\"2025-12-19 13:11:10\",\"flowid\":\"351\"}";
            StringEntity entity = new StringEntity(data, Charset.forName("UTF-8"));
            entity.setContentType("application/json");
            Header[] headers = new Header[1];
            Header header = new BasicHeader("Content-Type", "application/json");
            headers[0] = header;
            String rtnMsg = httpHelper.Post(url, entity, headers);
            System.out.println("测试请求返回rtnMsg：" + rtnMsg);
            JSONObject joRtn = JSONObject.parseObject(rtnMsg);
            return "接口返回成功";
        } catch (Exception ex) {
             ex.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/getData4", method = RequestMethod.GET)
    @ResponseBody
    public Object getData4() throws Exception {
        try {
            String url = "http://10.10.10.151:8888/wui/index.html";
            Header[] headers = new Header[1];
            Header header = new BasicHeader("Content-Type", "application/xml");
            headers[0] = header;
            String rtnMsg = httpHelper.Get(url, headers);
            System.out.println("测试请求返回rtnMsg：" + rtnMsg);
            JSONObject joRtn = JSONObject.parseObject(rtnMsg);
            return "接口返回成功";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/getData5", method = RequestMethod.POST)
    @ResponseBody
    public Object getData5(@RequestParam(value="unisocialCredit", required = false) String unisocialCredit) throws Exception {
        try {
            Thread thread = new Thread(() -> {
                enterpriseService.syncEnterpriseAttachment(unisocialCredit);
            });
            thread.start();
            return "同步供应商附件接口返回成功";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
