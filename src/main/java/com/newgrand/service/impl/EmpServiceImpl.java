package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.newgrand.domain.dto.EmpModel;
import com.newgrand.domain.dto.SyncResultDto;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.domain.po.HrEpmMain;
import com.newgrand.service.EmpService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.service.mp.HrEpmMainService;
import com.newgrand.utils.i8util.BoPoMoFoUtil;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmpServiceImpl implements EmpService {

    private final HrEpmMainService hrEpmMainService;
    private final FgOrglistService fgOrglistService;
    private final BoPoMoFoUtil boPoMoFoUtil;
    private final I8Request i8Request;
    private final UipLog uipLog;
    @Autowired
    private ThreadPoolExecutor pool;

    private static final String mstformDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"CNo\":\"test0514\",\"CName\":\"测试员工01\",\"PetName\":\"测试员工01\",\"ShortName\":\"cesyg01\",\"SexNo\":\"1\",\"CardType\":\"101\",\"CardNo\":\"320281200012273517\",\"Birthday\":\"2000-12-27\",\"Dept\":\"823000000000004\",\"CBoo\":\"614210413000001\",\"Station\":\"823000000000008\",\"Position\":\"823000000000003\",\"Pgrand\":\"823000000000003\",\"Plevel\":\"0\",\"CDt\":\"2025-05-14\",\"EmpType\":\"1022\",\"EmpStatus\":\"32\",\"LawTest\":\"\",\"Lawno\":\"\",\"Mobile1\":\"18861745687\",\"HomeTell\":\"\",\"Tell2\":\"\",\"Email\":\"\",\"LinkMan\":\"\",\"LinkManMobile\":\"\",\"Seq\":\"3\",\"DimissDt\":\"\",\"Issecuser\":\"1\",\"SyNetCall\":\"1\",\"IsWeChat\":\"0\",\"FreeShow\":\"1\",\"IsBuyOperator\":\"0\",\"IsOperator\":\"0\",\"Projectmanager\":\"0\",\"user_fw_id\":\"\",\"user_craft_type\":\"\",\"ProjRolesPhid\":\"\",\"BoPoMoFo\":\"CSYG01\",\"PhId\":\"\",\"Creator\":\"\",\"Editor\":\"\",\"CurOrgId\":\"\",\"NgInsertDt\":\"\",\"NgUpdateDt\":\"\",\"NgRecordVer\":\"\",\"Py\":\"CSYG01\",\"CompanyMobile\":\"\",\"BasePhId\":\"\",\"LinkPhId\":\"\",\"StationPhId\":\"\",\"BaseNgRecordVer\":\"\",\"LinkNgRecordVer\":\"\",\"StationNgRecordVer\":\"\",\"StationBdt\":\"\",\"PositionGrade\":\"823000000000003\",\"ImageType\":\"\",\"EmpId\":\"\",\"IsCur\":\"\",\"BillType\":\"\",\"CheckInPhId\":\"\",\"user_yy_id\":\"\",\"key\":\"\"}}}";

    @Override
    public I8ReturnModel saveEmp(EmpModel data) {
        log.info("人员同步入参: {}", JSONObject.toJSONString(data));
        try {
            LambdaQueryWrapper<HrEpmMain> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HrEpmMain::getEmpno, data.getCNo());
            List<HrEpmMain> list = hrEpmMainService.list(queryWrapper);
            if (list.isEmpty()) {
                //新增
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();

                mapInfo.put("CNo", data.getCNo());
                mapInfo.put("CName", data.getCName());
                mapInfo.put("PetName", data.getCName());
                mapInfo.put("ShortName", boPoMoFoUtil.getChineseInitials(data.getCName()));
                mapInfo.put("SexNo", data.getSexNo());
                mapInfo.put("CardType", "101"); //TODO 暂时全填身份证
                mapInfo.put("CardNo", data.getCardNo());
                mapInfo.put("Birthday", data.getBirthday());

                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getOcode, data.getDept());
                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                if (list1.isEmpty()) {
                    throw new RuntimeException("员工所在部门尚未同步进系统");
                }
                FgOrglist fgOrglist = list1.get(0);
                mapInfo.put("Dept", fgOrglist.getPhid());
                mapInfo.put("CBoo", fgOrglist.getParentOrgid());

                mapInfo.put("Station", "");
                mapInfo.put("Position", "");
                mapInfo.put("Pgrand", "");
                mapInfo.put("CDt", data.getCdt());
                mapInfo.put("EmpType", "1022"); //TODO 员工状态暂时都填全职
                mapInfo.put("EmpStatus", data.getEmpStatus());
                mapInfo.put("Mobile1", data.getMobile());
                mapInfo.put("PositionGrade", "");

                String mstFormData = I8Converter.SetField(mstformDataStr, mapInfo);

                urlParameters.add(new BasicNameValuePair("mstformData", mstFormData));
                urlParameters.add(new BasicNameValuePair("flag", "0"));
                urlParameters.add(new BasicNameValuePair("busguid", ""));
                urlParameters.add(new BasicNameValuePair("id", "-1"));
                urlParameters.add(new BasicNameValuePair("asr_fid", ""));
                urlParameters.add(new BasicNameValuePair("asr_guid", ""));
                urlParameters.add(new BasicNameValuePair("HoldPostInfoData", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("ng3_logid", "315211026000006"));
                log.info("调用的param:{}", JSONObject.toJSONString(urlParameters));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/HR/Emp/HrEpmMain/save", urlParameters);
                log.info(data.getCName() + "---同步结果: {}", JSONObject.toJSONString(i8ReturnModel));
                if (!i8ReturnModel.getIsOk()) {
                    return i8ReturnModel;
                }
                return I8ResultUtil.success("员工" + data.getCName() + "新增成功");
            } else {
                return I8ResultUtil.success("员工" + data.getCName() + "更新成功");
            }
        } catch (Exception e) {
            return I8ResultUtil.error(e.getMessage());
        }
    }

    @Override
    public I8ReturnModel saveEmpList(List<EmpModel> list){
        log.info("人员批量同步入参: {}", JSONObject.toJSONString(list));
        try {
            List<String> cNOList = list.stream().map(EmpModel::getCNo).filter(Objects::nonNull).collect(Collectors.toList());
            List<HrEpmMain> hrEpmMainList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(cNOList)) {
                LambdaQueryWrapper<HrEpmMain> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(HrEpmMain::getEmpno, cNOList);
                hrEpmMainList = hrEpmMainService.list(queryWrapper);
            }
            //获取新增和更新
            List<EmpModel> addList = new ArrayList<>();
            List<HrEpmMain> updateList = new ArrayList<>();
            for (EmpModel empModel : list) {
               List<HrEpmMain> hrList = hrEpmMainList.stream().filter(s-> s.getEmpno()!=null && s.getEmpno().equals(empModel.getCNo())).collect(Collectors.toList());
               if (CollectionUtil.isNotEmpty(hrList)) {
                   HrEpmMain hrEpmMain = hrList.get(0);
                   updateList.add(hrEpmMain);
               } else {
                   addList.add(empModel);
               }
            }

            //更新
            if (CollectionUtil.isNotEmpty(updateList)) {
                hrEpmMainService.saveOrUpdateBatch(updateList);
            }

            //新增
            List<I8ReturnModel> failList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(addList)) {
                for (EmpModel data:addList) {
                   // pool.execute(new Runnable() {
                     //   public void run() {
                            try {
                                //新增
                                List<NameValuePair> urlParameters = new ArrayList<>();
                                HashMap<String, Object> mapInfo = new HashMap<>();
                                mapInfo.put("CNo", data.getCNo());
                                mapInfo.put("CName", data.getCName());
                                mapInfo.put("PetName", data.getCName());
                                mapInfo.put("ShortName", boPoMoFoUtil.getChineseInitials(data.getCName()));
                                mapInfo.put("SexNo", data.getSexNo());
                                mapInfo.put("CardType", "101"); //TODO 暂时全填身份证
                                mapInfo.put("CardNo", data.getCardNo());
                                mapInfo.put("Birthday", data.getBirthday());

                                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                                queryWrapper1.eq(FgOrglist::getOcode, data.getDept());
                                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                                if (list1.isEmpty()) {
                                    throw new RuntimeException("员工所在部门尚未同步进系统"+ "  员工编码："+ data.getCNo() + "部门编码：" + data.getDept());
                                }
                                if (StringUtils.isEmpty(data.getMobile())) {
                                    //手机号为空 不同步
                                    log.info("手机号为空 不同步" + "  员工编码：" + data.getCNo());
                                    continue;
                                }
                                FgOrglist fgOrglist = list1.get(0);
                                mapInfo.put("Dept", fgOrglist.getPhid());
                                mapInfo.put("CBoo", fgOrglist.getParentOrgid());
                                mapInfo.put("Station", "");
                                mapInfo.put("Position", "");
                                mapInfo.put("Pgrand", "");
                                mapInfo.put("CDt", data.getCdt());
                                mapInfo.put("EmpType", "1022"); //TODO 员工状态暂时都填全职
                                mapInfo.put("EmpStatus", data.getEmpStatus());
                                mapInfo.put("Mobile1", data.getMobile());
                                mapInfo.put("PositionGrade", "");

                                String mstFormData = I8Converter.SetField(mstformDataStr, mapInfo);

                                urlParameters.add(new BasicNameValuePair("mstformData", mstFormData));
                                urlParameters.add(new BasicNameValuePair("flag", "0"));
                                urlParameters.add(new BasicNameValuePair("busguid", ""));
                                urlParameters.add(new BasicNameValuePair("id", "-1"));
                                urlParameters.add(new BasicNameValuePair("asr_fid", ""));
                                urlParameters.add(new BasicNameValuePair("asr_guid", ""));
                                urlParameters.add(new BasicNameValuePair("HoldPostInfoData", "{\"table\":{\"key\":\"PhId\"}}"));
                                urlParameters.add(new BasicNameValuePair("ng3_logid", "315211026000006"));
                                log.info("调用人员同步的param:{}", JSONObject.toJSONString(urlParameters));
                                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/HR/Emp/HrEpmMain/save", urlParameters);
                                log.info(data.getCName() + "---人员同步结果: {}", JSONObject.toJSONString(i8ReturnModel));
                                if (!i8ReturnModel.getIsOk()) {
                                    //失败
                                    log.info("员工" + data.getCName() + "新增失败");
                                    String msg = i8ReturnModel.getMessage();
                                    if (msg!=null) {
                                        msg = msg + "  员工编码："+ data.getCNo() + "手机号：" + data.getMobile();
                                    } else {
                                        msg = " 员工编码："+ data.getCNo() + "手机号：" + data.getMobile();
                                    }
                                    i8ReturnModel.setMessage(msg);
                                    failList.add(i8ReturnModel);
                                } else {
                                    //成功
                                    log.info("员工" + data.getCName() + "新增成功");
                                }
                            } catch (Exception e) {
                                I8ReturnModel i8ReturnModel = new I8ReturnModel();
                                i8ReturnModel.setMessage(e.getMessage() + " 员工编码:" + data.getCNo());
                                failList.add(i8ReturnModel);
                            }
                      //  }
                   // });
                }
            }
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(list.size() - failList.size());
            syncResultDto.setFail(failList.size());
            syncResultDto.setMsg(new ArrayList<>());
            for (I8ReturnModel  i8ReturnModel : failList) {
                syncResultDto.getMsg().add(i8ReturnModel.getMessage());
            }
            return I8ResultUtil.success("员工批量同步更新返回", syncResultDto);
        } catch (Exception e) {
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(0);
            syncResultDto.setFail(list.size());
            syncResultDto.setMsg(new ArrayList<>());
            syncResultDto.getMsg().add(e.getMessage());
            return I8ResultUtil.error(e.getMessage(), syncResultDto);
        }
    }

}
