package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.OrgSyncRequest;
import com.newgrand.domain.dto.SyncResultDto;
import com.newgrand.domain.dto.UpdateActiveRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.service.OrgService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.utils.StringUtils;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService {

    private final I8Request i8Request;
    private final FgOrglistService fgOrglistService;

    @Autowired
    private ThreadPoolExecutor pool;

    private static final String organizationFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"OCode\":\"test250409\",\"OName\":\"测试组织250409\",\"Bopomofo\":\"\",\"CodeValue\":\"\",\"OrgType\":\"Y\",\"IsActive\":\"1\",\"CloseOrgDt\":\"\",\"EbDt\":\"\",\"EeDt\":\"\",\"GfiYear\":\"\",\"GfiCboo\":\"\",\"RelatGfiCboo\":\"\",\"OrgOwner\":\"\",\"Financer\":\"\",\"Accounter\":\"\",\"IsInner\":0,\"AdminOrg\":0,\"OMemo\":\"\",\"attrcode14\":\"14\",\"attrcode18\":\"18\",\"attrcode23\":\"23\",\"attrcode30\":\"30\",\"attrcode45\":\"45\",\"attrcode46\":\"46\",\"OrgNatura\":\"\",\"FinanceCheck\":\"\",\"CheckDept\":\"\",\"RelProject\":\"\",\"BelongDept\":\"\",\"WeChatId\":\"\",\"IsColleague\":\"\",\"PhId\":\"\",\"OrgIndex\":\"\",\"OrgLabel\":\"\",\"IsWeChat\":\"0\",\"IsNfcShow\":\"0\",\"IsEnd\":\"0\",\"PersonFlg\":4,\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"\"}}}";
    private static final String enterpriseFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PersonFlg\":4,\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"PhId\":\"\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"\"}}}";
    private static final String deptOrganizationFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"OCode\":\"A001001.01\",\"OName\":\"集团行政管理中心\",\"Bopomofo\":\"\",\"CodeValue\":\"\",\"OrgType\":\"N\",\"IsActive\":\"1\",\"CloseOrgDt\":\"\",\"EbDt\":\"\",\"EeDt\":\"\",\"GfiYear\":\"\",\"GfiCboo\":\"\",\"RelatGfiCboo\":\"\",\"OrgOwner\":\"0\",\"Financer\":\"0\",\"Accounter\":\"0\",\"AdminOrg\":0,\"OMemo\":\"\",\"OrgNatura\":\"\",\"FinanceCheck\":0,\"CheckDept\":\"347181107000019\",\"RelProject\":\"0\",\"BelongDept\":0,\"WeChatId\":24,\"IsWeChat\":\"1\",\"IsColleague\":\"\",\"IsInner\":0,\"PhId\":\"347181107000019\",\"OrgIndex\":\"\",\"OrgLabel\":\"\",\"IsNfcShow\":\"0\",\"IsEnd\":\"0\",\"attrcode46\":\"0\",\"ParentOrgId\":\"\",\"EmpCode\":\"\",\"OTax\":\"\",\"DeptAttr\":\"\",\"key\":\"347181107000019\"}}}";
    private static final String deptEnterpriseformData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PersonFlg\":\"\",\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"PhId\":\"347181107000019\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"347181107000019\"}}}";


    @Override
    public I8ReturnModel saveOrgOrDept(OrgSyncRequest data) {
        I8ReturnModel saveResult = "Y".equals(data.getOrgType()) ? saveOrg(data) : saveDept(data);
        return saveResult;
    }

    @Override
    public I8ReturnModel saveOrgOrDeptList(List<OrgSyncRequest> list){
        log.info("组织批量同步入参: {}", JSONObject.toJSONString(list));
        List<String> userOfsIds = list.stream().map(OrgSyncRequest::getUserOfsid).filter(Objects::nonNull).collect(Collectors.toList());
        List<FgOrglist> fgOrglist = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userOfsIds)) {
            LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(FgOrglist::getUserOfsid, userOfsIds);
            fgOrglist = fgOrglistService.list(queryWrapper);
        }

        //获取新增和更新
        List<OrgSyncRequest> orgAddList = new ArrayList<>();
        List<OrgSyncRequest> deptAddList = new ArrayList<>();
        List<FgOrglist> updateList = new ArrayList<>();
        for (OrgSyncRequest orgSyncRequest : list) {
            List<FgOrglist> orgList = fgOrglist.stream().filter(s-> s.getUserOfsid()!=null && s.getUserOfsid().equals(orgSyncRequest.getUserOfsid())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(orgList)) {
                FgOrglist fgOrg = orgList.get(0);
                fgOrg.setOname(orgSyncRequest.getOName());
                updateList.add(fgOrg);
            } else {
                if ("Y".equals(orgSyncRequest.getOrgType())) {
                    orgAddList.add(orgSyncRequest);
                } else {
                    deptAddList.add(orgSyncRequest);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            fgOrglistService.saveOrUpdateBatch(updateList);
        }
        //批量新增组织
        SyncResultDto orgDto = saveOrgList(orgAddList);
        //批量新增部门
        SyncResultDto deptDto = saveDeptList(deptAddList);
        SyncResultDto dto = new SyncResultDto();
        dto.setSuccess(list.size() - orgDto.getFail() - deptDto.getFail());
        dto.setFail(orgDto.getFail() + deptDto.getFail());
        dto.setMsg(new ArrayList<>());
        dto.getMsg().addAll(orgDto.getMsg());
        dto.getMsg().addAll(deptDto.getMsg());
        return I8ResultUtil.success("组织/部门批量同步更新返回", dto);
    }

    public I8ReturnModel saveOrg(OrgSyncRequest data) {
        log.info("同步组织入参: {}", JSONObject.toJSONString(data));
        //uipLog.info("saveOrg", "同步组织入参", JSONObject.toJSONString(data));
        try {
            LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FgOrglist::getUserOfsid, data.getUserOfsid())
                    .eq(FgOrglist::getOrgtype, "Y");
            List<FgOrglist> list = fgOrglistService.list(queryWrapper);
            if (list.isEmpty()) {
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();
                mapInfo.put("OCode", data.getOCode());
                mapInfo.put("OName", data.getOName());
                mapInfo.put("OrgType", data.getOrgType());
                mapInfo.put("user_ofsid", data.getUserOfsid());
                mapInfo.put("user_fw_id", data.getUserFwId());
//                mapInfo.put("UnisocialCredit", data.getUnisocialCredit());

                //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                String parentOrg = data.getParentOrg();
                if(StringUtils.isNotBlank(parentOrg)) {
                    LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(FgOrglist::getUserOfsid, parentOrg)
                            .eq(FgOrglist::getOrgtype, "Y");
                    List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                    if (!list1.isEmpty()) {
                        FgOrglist fgOrglist = list1.get(0);
                        mapInfo.put("ParentOrgId", fgOrglist.getPhid());
                    }
                }

                String organizationFormDataStr = I8Converter.SetField(organizationFormData, mapInfo);

                HashMap<String, Object> mapInfo2 = new HashMap<>();
//                mapInfo2.put("UnisocialCredit", data.getUnisocialCredit());

                String enterpriseFormDataStr = I8Converter.SetField(enterpriseFormData, mapInfo2);

                urlParameters.add(new BasicNameValuePair("organizationformData", organizationFormDataStr));
                urlParameters.add(new BasicNameValuePair("enterpriseformData", enterpriseFormDataStr));
                urlParameters.add(new BasicNameValuePair("orgattrgridData", "14,18,23,30,45,46,"));
                urlParameters.add(new BasicNameValuePair("orgType", "Y"));
                urlParameters.add(new BasicNameValuePair("attachGuid", ""));
                urlParameters.add(new BasicNameValuePair("moduleRightDt", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("ng3_logid", "614210413000001"));

                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/save", urlParameters);
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("组织新增成功");
                }
            } else {
                LambdaUpdateWrapper<FgOrglist> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(FgOrglist::getUserOfsid, data.getUserOfsid())
                        .set(FgOrglist::getOname, data.getOName());
                if (fgOrglistService.update(updateWrapper)) {
                    return I8ResultUtil.success("组织更新成功");
                }
            }
            return I8ResultUtil.error(data.getOCode() + "--" + data.getOName() + ", 该组织同步失败");
        } catch (Exception e) {
            return I8ResultUtil.error(e.getMessage());
        }
    }

    public SyncResultDto saveOrgList(List<OrgSyncRequest> orgList) {
        log.info("批量同步新增组织入参: {}", JSONObject.toJSONString(orgList));
        try {
            //新增
            List<I8ReturnModel> failList = new ArrayList<>();
            for (OrgSyncRequest data : orgList) {
              //  pool.execute(new Runnable() {
               //     public void run() {
                        try {
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            HashMap<String, Object> mapInfo = new HashMap<>();
                            mapInfo.put("OCode", data.getOCode());
                            mapInfo.put("OName", data.getOName());
                            mapInfo.put("OrgType", data.getOrgType());
                            mapInfo.put("user_ofsid", data.getUserOfsid());
                            mapInfo.put("user_fw_id", data.getUserFwId());

                            //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                            String parentOrg = data.getParentOrg();
                            if(StringUtils.isNotBlank(parentOrg)) {
                                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                                queryWrapper1.eq(FgOrglist::getUserOfsid, parentOrg)
                                        .eq(FgOrglist::getOrgtype, "Y");
                                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                                if (!list1.isEmpty()) {
                                    FgOrglist fgOrglist = list1.get(0);
                                    mapInfo.put("ParentOrgId", fgOrglist.getPhid());
                                }
                            }
                            String organizationFormDataStr = I8Converter.SetField(organizationFormData, mapInfo);
                            HashMap<String, Object> mapInfo2 = new HashMap<>();
                            String enterpriseFormDataStr = I8Converter.SetField(enterpriseFormData, mapInfo2);
                            urlParameters.add(new BasicNameValuePair("organizationformData", organizationFormDataStr));
                            urlParameters.add(new BasicNameValuePair("enterpriseformData", enterpriseFormDataStr));
                            urlParameters.add(new BasicNameValuePair("orgattrgridData", "14,18,23,30,45,46,"));
                            urlParameters.add(new BasicNameValuePair("orgType", "Y"));
                            urlParameters.add(new BasicNameValuePair("attachGuid", ""));
                            urlParameters.add(new BasicNameValuePair("moduleRightDt", "{\"table\":{\"key\":\"PhId\"}}"));
                            urlParameters.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                    //        log.info("调用组织同步的param:{}", JSONObject.toJSONString(urlParameters));
                            I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/save", urlParameters);
                            log.info("组织请求返回：" + i8ReturnModel.toString() + "组织编码：" + data.getOCode());
                            if (i8ReturnModel.getIsOk()) {
                                log.info("组织新增成功，组织编码：" + data.getOCode());
                            } else {
                                log.info("组织新增失败，组织编码：" + data.getOCode());
                                String msg = i8ReturnModel.getMessage();
                                if (msg!=null) {
                                    msg = msg + "  组织编码："+ data.getOCode();
                                } else {
                                    msg = " 组织编码："+ data.getOCode();
                                }
                                i8ReturnModel.setMessage(msg);
                                failList.add(i8ReturnModel);
                            }
                        } catch (Exception e) {
                            I8ReturnModel i8ReturnModel = new I8ReturnModel();
                            i8ReturnModel.setMessage(e.getMessage() + " 组织编码: " + data.getOCode());
                            failList.add(i8ReturnModel);
                        }
                 //   }
              //  });
            }
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(orgList.size() - failList.size());
            syncResultDto.setFail(failList.size());
            syncResultDto.setMsg(new ArrayList<>());
            for (I8ReturnModel i8ReturnModel:failList) {
                syncResultDto.getMsg().add(i8ReturnModel.getMessage());
            }
            return syncResultDto;
        } catch (Exception e) {
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(0);
            syncResultDto.setFail(orgList.size());
            syncResultDto.setMsg(new ArrayList<>());
            syncResultDto.getMsg().add(e.getMessage());
            return syncResultDto;
        }
    }

    public I8ReturnModel saveDept(OrgSyncRequest data) {
        log.info("同步部门入参: {}", JSONObject.toJSONString(data));
        try {
            LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FgOrglist::getUserOfsid, data.getUserOfsid())
                    .eq(FgOrglist::getOrgtype, "N");
            List<FgOrglist> list = fgOrglistService.list(queryWrapper);
            if (list.isEmpty()) {
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();
                mapInfo.put("OCode", data.getOCode());
                mapInfo.put("OName", data.getOName());
                mapInfo.put("OrgType", data.getOrgType());
                mapInfo.put("user_ofsid", data.getUserOfsid());
                mapInfo.put("user_fw_id", data.getUserFwId());

                //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                String parentOrg = data.getParentOrg();
                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getUserOfsid, parentOrg)
                        .eq(FgOrglist::getOrgtype, "Y");
                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                if(list1.isEmpty()) {
                    return I8ResultUtil.error("部门同步失败 : 归属组织找不到");
                }
                FgOrglist fgOrglist = list1.get(0);
                mapInfo.put("ParentOrgId", fgOrglist.getPhid());
                mapInfo.put("EmpCode", data.getEmpcode());
                mapInfo.put("OTax", data.getOtax());

                String deptOrganizationFormDataStr = I8Converter.SetField(deptOrganizationFormData, mapInfo);

                urlParameters.add(new BasicNameValuePair("organizationformData", deptOrganizationFormDataStr));
                urlParameters.add(new BasicNameValuePair("enterpriseformData", deptEnterpriseformData));
                urlParameters.add(new BasicNameValuePair("orgattrgridData", ""));
                urlParameters.add(new BasicNameValuePair("orgType", "N"));
                urlParameters.add(new BasicNameValuePair("attachGuid", ""));
                urlParameters.add(new BasicNameValuePair("moduleRightDt", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("ng3_logid", "614210413000001"));

                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/save", urlParameters);
                System.out.println("请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("部门新增成功");
                }
            } else {
                LambdaUpdateWrapper<FgOrglist> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(FgOrglist::getOcode, data.getOCode())
                        .set(FgOrglist::getOname, data.getOName())
                        .set(FgOrglist::getOcode, data.getOCode())
                        .set(FgOrglist::getEmpcode, data.getEmpcode())
                        .set(FgOrglist::getOtax, data.getOtax())
                        .set(FgOrglist::getUserOfsid, data.getUserOfsid());
                if (fgOrglistService.update(updateWrapper)) {
                    return I8ResultUtil.success("部门更新成功");
                }
            }
            return I8ResultUtil.error(data.getOCode() + "--" + data.getOName() + ", 该部门同步失败");
        } catch (Exception e) {
            return I8ResultUtil.error("部门同步失败 : " + e.getMessage());
        }
    }

    public SyncResultDto saveDeptList(List<OrgSyncRequest> deptList) {
        log.info("批量同步新增部门入参: {}", JSONObject.toJSONString(deptList));
        try {
            //新增
            List<I8ReturnModel> failList = new ArrayList<>();
            for (OrgSyncRequest data : deptList) {
               // pool.execute(new Runnable() {
                //    public void run() {
                        try {
                            List<NameValuePair> urlParameters = new ArrayList<>();
                            HashMap<String, Object> mapInfo = new HashMap<>();
                            mapInfo.put("OCode", data.getOCode());
                            mapInfo.put("OName", data.getOName());
                            mapInfo.put("OrgType", data.getOrgType());
                            mapInfo.put("user_ofsid", data.getUserOfsid());
                            mapInfo.put("user_fw_id", data.getUserFwId());

                            //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                            String parentOrg = data.getParentOrg();
                            LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                            queryWrapper1.eq(FgOrglist::getUserOfsid, parentOrg)
                                    .eq(FgOrglist::getOrgtype, "Y");
                            List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                            if (list1.isEmpty()) {
                                log.info("部门同步失败 : 归属组织找不到,归属组织编码：" + parentOrg);
                                I8ReturnModel i8ReturnModel = new I8ReturnModel();
                                i8ReturnModel.setMessage("部门同步失败 : 归属组织找不到 组织编码:"+ data.getOCode() + "归属组织：" + parentOrg);
                                failList.add(i8ReturnModel);
                                continue;
                            }
                            FgOrglist fgOrglist = list1.get(0);
                            mapInfo.put("ParentOrgId", fgOrglist.getPhid());
                            mapInfo.put("EmpCode", data.getEmpcode());
                            mapInfo.put("OTax", data.getOtax());

                            String deptOrganizationFormDataStr = I8Converter.SetField(deptOrganizationFormData, mapInfo);

                            urlParameters.add(new BasicNameValuePair("organizationformData", deptOrganizationFormDataStr));
                            urlParameters.add(new BasicNameValuePair("enterpriseformData", deptEnterpriseformData));
                            urlParameters.add(new BasicNameValuePair("orgattrgridData", ""));
                            urlParameters.add(new BasicNameValuePair("orgType", "N"));
                            urlParameters.add(new BasicNameValuePair("attachGuid", ""));
                            urlParameters.add(new BasicNameValuePair("moduleRightDt", "{\"table\":{\"key\":\"PhId\"}}"));
                            urlParameters.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                      //      log.info("调用部门同步的param:{}", JSONObject.toJSONString(urlParameters));
                            I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/save", urlParameters);
                            log.info("部门请求返回：" + i8ReturnModel.toString() + "部门编码：" + data.getOCode());
                            if (i8ReturnModel.getIsOk()) {
                                log.info("部门新增成功，部门编码：" + data.getOCode());
                            } else {
                                log.info("部门新增失败，部门编码：" + data.getOCode());
                                String msg = i8ReturnModel.getMessage();
                                if (msg!=null) {
                                    msg = msg + "  部门编码："+ data.getOCode();
                                } else {
                                    msg = " 部门编码："+ data.getOCode();
                                }
                                i8ReturnModel.setMessage(msg);
                                failList.add(i8ReturnModel);
                            }
                        } catch (Exception e) {
                            I8ReturnModel i8ReturnModel = new I8ReturnModel();
                            i8ReturnModel.setMessage(e.getMessage() + " 部门编码: " + data.getOCode());
                            failList.add(i8ReturnModel);
                        }
                 //   }
               // });
            }
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(deptList.size() - failList.size());
            syncResultDto.setFail(failList.size());
            syncResultDto.setMsg(new ArrayList<>());
            for (I8ReturnModel i8ReturnModel:failList) {
                syncResultDto.getMsg().add(i8ReturnModel.getMessage());
            }
            return syncResultDto;
        } catch (Exception e) {
            SyncResultDto syncResultDto = new SyncResultDto();
            syncResultDto.setSuccess(0);
            syncResultDto.setFail(deptList.size());
            syncResultDto.setMsg(new ArrayList<>());
            syncResultDto.getMsg().add(e.getMessage());
            return syncResultDto;
        }
    }

    public I8ReturnModel updateActive(UpdateActiveRequest data){
        log.info("停用/启用组织入参: {}", JSONObject.toJSONString(data));
        LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FgOrglist::getUserOfsid, data.getId())
                    .eq(FgOrglist::getOrgtype, "Y");
            List<FgOrglist> list = fgOrglistService.list(queryWrapper);
            if (list.isEmpty()) {
                return I8ResultUtil.error("停用/启用组织失败 : 该组织没有同步");
            } else {
                List<NameValuePair> urlParameters = new ArrayList<>();
                urlParameters.add(new BasicNameValuePair("id", list.get(0).getPhid()));
                urlParameters.add(new BasicNameValuePair("active", data.getValue()));
                urlParameters.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/UpdateActive", urlParameters);
                System.out.println("请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("停用/启用组织成功");
                } else {
                    return I8ResultUtil.error("停用/启用组织失败："+i8ReturnModel.getMessage());
                }
            }
    }

}
