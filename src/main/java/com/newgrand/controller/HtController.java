package com.newgrand.controller;


import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.HtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "合同信息同步")
@RestController
@RequestMapping("/ht")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HtController {

    @Autowired
    private HtService htService;

    @ApiOperation(value = "合同信息同步到oa", notes = "合同信息同步接口")
    @RequestMapping(value = "/syncHt", method = RequestMethod.POST)
    I8ReturnModel syncHt(@RequestParam("billNo") String billNo) {
        return htService.syncHt(billNo);
    }

    @ApiOperation(value = "合同信息同步到oa", notes = "合同信息同步接口")
    @RequestMapping(value = "/syncHtById", method = RequestMethod.GET)
    I8ReturnModel syncHtById(@RequestParam("phid") Long phid) {
        return htService.syncHtById(phid);
    }
}
