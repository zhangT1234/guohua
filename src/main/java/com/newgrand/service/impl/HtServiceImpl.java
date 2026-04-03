package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.ProjectTableModel;
import com.newgrand.domain.po.Pcm3CntM;
import com.newgrand.service.HtService;
import com.newgrand.service.ProjectTableService;
import com.newgrand.service.mp.Pcm3CntMService;
import com.newgrand.utils.OaRequestUtil;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HtServiceImpl implements HtService {

    @Autowired
    private Pcm3CntMService pcm3CntMService;
    @Autowired
    private OaRequestUtil oaRequestUtil;
    @Autowired
    private ProjectTableService projectTableService;

    @Override
    public I8ReturnModel syncHt(String billNo){
        try {
            LambdaQueryWrapper<Pcm3CntM> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Pcm3CntM::getBillNo, billNo);
            List<Pcm3CntM> list = pcm3CntMService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                LambdaQueryWrapper<ProjectTableModel> projWrapper = new LambdaQueryWrapper<>();
                projWrapper.eq(ProjectTableModel::getPhid, list.get(0).getPhidPc());
                List<ProjectTableModel> projList = projectTableService.list(projWrapper);
                String projNo = "";
                if (CollectionUtil.isNotEmpty(projList)) {
                    projNo = projList.get(0).getPcNo();
                }
                String body = "bm=" + list.get(0).getBillNo() + "&mc=" + list.get(0).getTitle() + "&ssxm=" + projNo;
                OaResult oaResult = oaRequestUtil.sendPost("/api/toOa/receivedHt", body);
                if ("0".equals(oaResult.getCode())) {
                    return I8ResultUtil.success(oaResult.getMsg() != null ? oaResult.getMsg() : "合同同步更新成功", oaResult.getData());
                } else {
                    return I8ResultUtil.error(oaResult.getMsg() != null ? oaResult.getMsg() : "合同同步更新失败", oaResult.getData());
                }
            } else {
                return I8ResultUtil.error("该编码不存在合同数据");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return I8ResultUtil.error("合同同步失败：" + ex.getMessage());
        }
    }

    @Override
    public I8ReturnModel syncHtById(Long phid){
        try {
            LambdaQueryWrapper<Pcm3CntM> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Pcm3CntM::getPhid, phid);
            List<Pcm3CntM> list = pcm3CntMService.list(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)) {
                LambdaQueryWrapper<ProjectTableModel> projWrapper = new LambdaQueryWrapper<>();
                projWrapper.eq(ProjectTableModel::getPhid, list.get(0).getPhidPc());
                List<ProjectTableModel> projList = projectTableService.list(projWrapper);
                String projNo = "";
                if (CollectionUtil.isNotEmpty(projList)) {
                    projNo = projList.get(0).getPcNo();
                }
                String body = "bm=" + list.get(0).getBillNo() + "&mc=" + list.get(0).getTitle() + "&ssxm=" + projNo;
                OaResult oaResult = oaRequestUtil.sendPost("/api/toOa/receivedHt", body);
                if ("0".equals(oaResult.getCode())) {
                    return I8ResultUtil.success(oaResult.getMsg() != null ? oaResult.getMsg() : "合同同步更新成功", oaResult.getData());
                } else {
                    return I8ResultUtil.error(oaResult.getMsg() != null ? oaResult.getMsg() : "合同同步更新失败", oaResult.getData());
                }
            } else {
                return I8ResultUtil.error("该phid不存在合同数据");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            return I8ResultUtil.error("合同同步失败：" + ex.getMessage());
        }
    }

}
