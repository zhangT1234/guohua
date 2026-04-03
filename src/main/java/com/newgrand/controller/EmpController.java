package com.newgrand.controller;

import com.newgrand.domain.dto.EmpModel;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.EmpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: zhanglixin
 * @Data: 2022/11/19 12:41
 * @Description: TODO
 */
@Slf4j
@Api(tags = "员工信息同步")
@RestController
@RequestMapping("/emp")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EmpController {

    @Resource
    private EmpService empService;

    @ApiOperation(value = "人员保存接口", notes = "人员保存接口")
    @RequestMapping(value = "/saveEmp1", method = RequestMethod.POST)
    I8ReturnModel saveEmp(@RequestBody EmpModel data) {
        return empService.saveEmp(data);
    }

    @ApiOperation(value = "人员批量保存接口", notes = "人员批量保存接口")
    @RequestMapping(value = "/saveEmp", method = RequestMethod.POST)
    I8ReturnModel saveEmpList(@RequestBody List<EmpModel> list) {
        return empService.saveEmpList(list);
    }
}
