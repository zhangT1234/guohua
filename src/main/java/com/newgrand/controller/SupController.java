package com.newgrand.controller;

import com.newgrand.domain.dto.SupSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.SupService;
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
@Api(tags = "供应商信息同步")
@RestController
@RequestMapping("/sup")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupController {
    @Resource
    private SupService supService;

    @ApiOperation(value = "供应商信息同步接口", notes = "供应商同步接口")
    @RequestMapping(value = "/saveSup", method = RequestMethod.POST)
    I8ReturnModel saveData(@RequestBody @Valid SupSyncRequest data) {
        return supService.saveData(data);
    }
}
