package com.newgrand.controller;

import com.newgrand.domain.dto.AccRequest;
import com.newgrand.domain.dto.BillRequest;
import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api(tags = "付款单向OA发起流程")
@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BillController {

    @Autowired
    private BillService billService;

    @ApiOperation(value = "付款单发起OA流程", notes = "付款单发起OA流程")
    @RequestMapping(value = "/paybillsend", method = RequestMethod.GET)
    I8ReturnModel payBillOaWorkflow(@RequestParam("phid") Long phid) {
        return billService.payBillOaWorkflow(phid);
    }

    @ApiOperation(value = "项目其他支出单发起OA流程", notes = "项目其他支出单发起OA流程")
    @RequestMapping(value = "/otherpaysend", method = RequestMethod.GET)
    I8ReturnModel otherPayOaWorkflow(@RequestParam("phid") Long phid) {
        return billService.otherPayOaWorkflow(phid);
    }

}
