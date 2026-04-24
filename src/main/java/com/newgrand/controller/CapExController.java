package com.newgrand.controller;

import com.newgrand.domain.dto.CapExAttachRequest;
import com.newgrand.domain.dto.CapExRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.CapExService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "资金支出同步字段值")
@RestController
@RequestMapping("/capex")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CapExController {

    @Autowired
    private CapExService capExService;

    @ApiOperation(value = "资金支出同步字段值", notes = "资金支出同步字段值接口")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    I8ReturnModel sync(@RequestBody CapExRequest capExRequest) {
        return capExService.updateCapEx(capExRequest);
    }

    @ApiOperation(value = "回调同步附件", notes = "回调同步附件接口")
    @RequestMapping(value = "/updateAttachment", method = RequestMethod.POST)
    I8ReturnModel updateAttachment(@RequestBody CapExAttachRequest capExAttachRequest) {
        return capExService.updateAttachment(capExAttachRequest);
    }

}
