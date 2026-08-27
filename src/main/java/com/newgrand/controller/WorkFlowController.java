package com.newgrand.controller;

import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.impl.WorkFlowService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhanglixin
 * @Data: 2022/10/19 20:07
 * @Description: TODO
 */
@Slf4j
@Api(tags = "i8工作流待办推送BIP系统")
@RestController
@RequestMapping("/wf")
public class WorkFlowController {
    @Autowired
    private WorkFlowService workFlowService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public @ResponseBody
    I8ReturnModel Send(@RequestBody String jsonStr) throws Exception {
        return workFlowService.Send(jsonStr);
    }
}
