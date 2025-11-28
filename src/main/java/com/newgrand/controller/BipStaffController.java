package com.newgrand.controller;

import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import com.newgrand.domain.dto.BipStaffDTO;
import com.newgrand.domain.dto.ProjectSyncRequest;
import com.newgrand.service.BipStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@Api(tags = "人员信息同步（i8 -> BIP）")
@RestController
@RequestMapping("/bipStaff")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BipStaffController {
    @Resource
    private BipStaffService bipStaffService;

    @ApiOperation("同步人员信息到BIP")
    @GetMapping("/syncStaff")
    public BipResult syncStaff(@RequestParam("cNo") String cNo) {
        return bipStaffService.syncStaff(cNo);
    }

    @ApiOperation("测试同步人员信息到BIP")
    @PostMapping("/syncStaff")
    public BipResult testSyncStaff(@RequestBody BipRequest<BipStaffDTO> data) {
        return bipStaffService.testSyncStaff(data);
    }
}
