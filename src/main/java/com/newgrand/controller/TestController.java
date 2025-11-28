package com.newgrand.controller;

import com.newgrand.mapper.UIPCommonMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public @ResponseBody
    Object GetData() throws Exception {
        try {

            return "接口返回成功";
        } catch (Exception ex) {
        }
        return null;
    }

    @RequestMapping(value = "/getData2", method = RequestMethod.GET)
    public @ResponseBody
    Object GetData2() throws Exception {
        try {
            List<Map<String, Object>> listParent = uipCommonMapper.dynamicSql("select * from fg_orglist");
            return listParent;
        } catch (Exception ex) {
        }
        return null;
    }
}
