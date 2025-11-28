package com.newgrand.controller;

import com.newgrand.domain.dto.BipReceivableDTO;
import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import com.newgrand.service.BipReceivableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "应收单同步（i8 -> BIP）")
@RestController
@RequestMapping("/bipReceivable")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReceivableController {

    private final BipReceivableService bipReceivableService;

    @ApiOperation("测试同步应收单到BIP")
    @PostMapping("/testSyncReceivable")
    public BipResult testSyncReceivable(@RequestBody BipRequest<BipReceivableDTO> data) {
        return bipReceivableService.testSyncReceivable(data);
    }

    @ApiOperation("实际产值填报同步到产值单")
    @GetMapping("/factCz")
    public BipResult factCz(@RequestParam("billNo") String billNo) {
        return bipReceivableService.factCz(billNo);
    }

    @ApiOperation("其他收入产值填报同步到产值单")
    @GetMapping("/otherCz")
    public BipResult otherCz(@RequestParam("billNo") String billNo) {
        return bipReceivableService.otherCz(billNo);
    }

//    @ApiOperation("其他收入合同计量结算同步到产值单")
//    @GetMapping("/otherSettlement")
//    public BipResult otherSettlement(@RequestParam("billNo") String billNo) {
//        return bipReceivableService.otherSettlement(billNo);
//    }

    @ApiOperation("承包合同计量结算同步到计量单")
    @GetMapping("/contractSettlementJl")
    public BipResult contractSettlementJl(@RequestParam("billNo") String billNo) {
        return bipReceivableService.contractSettlementJl(billNo);
    }

    @ApiOperation("其他收入合同计量结算同步到计量单")
    @GetMapping("/otherSettlementJl")
    public BipResult otherSettlementJl(@RequestParam("billNo") String billNo) {
        return bipReceivableService.otherSettlementJl(billNo);
    }

//    @ApiOperation("其他收入合同计量结算同步")
//    @GetMapping("/factCz")
//    public BipResult factCz(@RequestParam("billNo") String billNo) {
//        return bipReceivableService.factCz(billNo);
//    }
//
//    @ApiOperation("材料差额应收单同步")
//    @GetMapping("/factCz")
//    public BipResult factCz(@RequestParam("billNo") String billNo) {
//        return bipReceivableService.factCz(billNo);
//    }
}
