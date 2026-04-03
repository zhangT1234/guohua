package com.newgrand.controller;

import com.newgrand.domain.dto.OrgSyncRequest;
import com.newgrand.domain.dto.UpdateActiveRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.OrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@Api(tags = "组织信息同步")
@RestController
@RequestMapping("/org")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrgController {

    @Resource
    private OrgService orgService;

    @ApiOperation(value = "组织/部门信息同步接口", notes = "组织同步接口")
    @RequestMapping(value = "/saveOrg1", method = RequestMethod.POST)
    I8ReturnModel saveData(@RequestBody @Valid OrgSyncRequest data) {
        return orgService.saveOrgOrDept(data);
    }

    @ApiOperation(value = "组织/部门信息批量同步接口", notes = "组织批量同步接口")
    @RequestMapping(value = "/saveOrg", method = RequestMethod.POST)
    I8ReturnModel saveOrgList(@RequestBody @Valid List<OrgSyncRequest> list) {
        return orgService.saveOrgOrDeptList(list);
    }

    @ApiOperation(value = "停用/启用组织", notes = "停用/启用组织接口")
    @RequestMapping(value = "/updateActive", method = RequestMethod.POST)
    I8ReturnModel updateActive(@RequestBody @Valid UpdateActiveRequest data) {
        return orgService.updateActive(data);
    }

}
