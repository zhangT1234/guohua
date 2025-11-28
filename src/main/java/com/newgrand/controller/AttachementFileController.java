package com.newgrand.controller;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.model.WorkFlowModel;
import com.newgrand.service.AttachmentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/7/12 11:14
 */
@Api(tags = "附件下载接口")
@RequestMapping("/Attach")
@RestController
public class AttachementFileController {

    @Autowired
    private AttachmentService attachService;


    /**
     * 用来下载文件的地址
     * 参数全部采用从地址来取的方式
     *
     * @param httpServletResponse 返回response
     * @param asr_code            asr_code
     * @param asr_table           asr_table
     * @param asr_attach_table    asr_attach_table
     * @param asr_filename        asr_filename
     * @throws IOException exception
     */
    @GetMapping("/download/{asr_code}/{asr_table}/{asr_attach_table}/{asr_filename}")
    void download(HttpServletResponse httpServletResponse, @PathVariable("asr_code") String asr_code,
                  @PathVariable("asr_table") String asr_table,
                  @PathVariable("asr_attach_table") String asr_attach_table,
                  @PathVariable("asr_filename") String asr_filename) throws IOException {
        attachService.downLoad(httpServletResponse, asr_code, asr_table, asr_attach_table, asr_filename);
    }

    @GetMapping("/test")
    void sync(@RequestBody String body) {
        List<WorkFlowModel> work = JSONObject.parseArray(body, WorkFlowModel.class);
        return;
    }
}