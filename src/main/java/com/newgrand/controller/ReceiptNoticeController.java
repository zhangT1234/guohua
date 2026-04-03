package com.newgrand.controller;

import com.newgrand.domain.dto.ReceiptNoticeRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.ReceiptNoticeService;
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
@Api(tags = "到账通知单同步")
@RestController
@RequestMapping("/receiptnotice")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptNoticeController {

    @Autowired
    private ReceiptNoticeService receiptNoticeService;

    @ApiOperation(value = "到账通知单同步接口", notes = "到账通知单同步接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    I8ReturnModel save(@RequestBody @Valid ReceiptNoticeRequest data) {
        return receiptNoticeService.saveReceiptNotice(data);
    }

}
