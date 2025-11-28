package com.newgrand.controller;

import com.newgrand.domain.dto.CusSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.CusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@Api(tags = "客户信息同步")
@RestController
@RequestMapping("/cus")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CusController {
    @Resource
    private CusService cusService;

    @ApiOperation(value = "客户信息同步接口", notes = "客户同步接口")
    @RequestMapping(value = "/saveCus", method = RequestMethod.POST)
    I8ReturnModel saveData(@RequestBody @Valid CusSyncRequest data) {
        return cusService.saveData(data);
    }
}
