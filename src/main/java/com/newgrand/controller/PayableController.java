package com.newgrand.controller;

import com.newgrand.domain.dto.BipPayableDTO;
import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import com.newgrand.service.BipPayableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "应付单同步（i8 -> BIP）")
@RestController
@RequestMapping("/bipPayable")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PayableController {
    private final BipPayableService bipPayableService;

    @ApiOperation("测试同步应付单到BIP")
    @PostMapping("/testSyncReceivable")
    public BipResult testSyncPayable(@RequestBody BipRequest<BipPayableDTO> data) {
        return bipPayableService.testSyncPayable(data);
    }

    @ApiOperation("劳务分包合同计量结算同步到发票应付")
    @GetMapping("/labor")
    public BipResult labor(@RequestParam("billNo") String billNo) {
        return bipPayableService.labor(billNo);
    }

    @ApiOperation("其他支出合同计量结算同步到发票应付")
    @GetMapping("/other")
    public BipResult other(@RequestParam("billNo") String billNo) {
        return bipPayableService.other(billNo);
    }

    @ApiOperation("采购合同计量结算同步到发票应付")
    @GetMapping("/purchase")
    public BipResult purchase(@RequestParam("billNo") String billNo) {
        return bipPayableService.purchase(billNo);
    }

    @ApiOperation("盘点单同步到发票应付")
    @GetMapping("/check")
    public BipResult check(@RequestParam("billNo") String billNo) {
        return bipPayableService.check(billNo);
    }
}
