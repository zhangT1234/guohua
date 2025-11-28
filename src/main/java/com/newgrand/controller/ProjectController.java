package com.newgrand.controller;

import com.newgrand.domain.dto.ProjectSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
