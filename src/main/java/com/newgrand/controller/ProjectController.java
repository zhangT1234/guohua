package com.newgrand.controller;

import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.dto.ProjectSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@Api(tags = "项目信息同步")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectController {
    @Resource
    private ProjectService projectService;

    @ApiOperation(value = "项目信息同步接口", notes = "项目同步接口")
    @RequestMapping(value = "/saveProject", method = RequestMethod.POST)
    I8ReturnModel saveData(@RequestBody @Valid ProjectSyncRequest data) {
        return projectService.saveData(data);
    }

    @ApiOperation(value = "项目信息同步到oa", notes = "项目同步接口")
    @RequestMapping(value = "/syncProj", method = RequestMethod.POST)
    I8ReturnModel syncProj(@RequestParam("pcNo") String pcNo) {
        return projectService.syncProj(pcNo);
    }

    @ApiOperation(value = "项目信息同步到oa", notes = "项目同步接口")
    @RequestMapping(value = "/syncProjById", method = RequestMethod.GET)
    I8ReturnModel syncProjById(@RequestParam("phid") Long phid) {
        return projectService.syncProjById(phid);
    }

}
