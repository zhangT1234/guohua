package com.newgrand.controller;

import com.newgrand.domain.dto.AccRequest;
import com.newgrand.domain.dto.EmpModel;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Api(tags = "收款账户信息同步")
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "收款账户保存接口", notes = "收款账户保存接口")
    @RequestMapping(value = "/saveAccount", method = RequestMethod.POST)
    I8ReturnModel save(@RequestBody @Valid AccRequest data) {
        return accountService.save(data);
    }

}
