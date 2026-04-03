package com.newgrand.controller;


import com.newgrand.domain.dto.ItemDataRequest;
import com.newgrand.domain.dto.ItemResRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.ItemService;
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
@Api(tags = "资源同步")
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDataController {

    @Resource
    private ItemService itemService;

    @ApiOperation(value = "资源分类同步接口", notes = "资源分类同步接口")
    @RequestMapping(value = "/saveRes", method = RequestMethod.POST)
    I8ReturnModel saveItemResData(@RequestBody @Valid ItemResRequest data) {
        return itemService.saveItemResData(data);
    }

    @ApiOperation(value = "资源主文件同步接口", notes = "资源主文件同步接口")
    @RequestMapping(value = "/saveData", method = RequestMethod.POST)
    I8ReturnModel saveData(@RequestBody @Valid ItemDataRequest data) {
        return itemService.saveItemData(data);
    }

}
