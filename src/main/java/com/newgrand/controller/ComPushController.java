package com.newgrand.controller;

import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.ComPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/10/8 14:26
 */
@Api(tags = "公共推送接口")
@RequestMapping("/Com")
@RestController
@CrossOrigin
public class ComPushController {
    @Resource
    private ComPushService comPushService;

    @ApiOperation(value = "公共推送接口", notes = "公共推送接口", produces = "application/json")
    @RequestMapping(value = "/Push/{sign}", method = RequestMethod.GET)
    I8ReturnModel pushData(@PathVariable("sign") String sign, String phid) {
        return comPushService.push(sign, phid);
    }
}
