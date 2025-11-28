package com.newgrand.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.OrgSyncRequest;
import com.newgrand.domain.model.FgOrgrelatitem;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.service.OrgService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.service.mp.FgOrgrelatitemService;
import com.newgrand.utils.i8util.BoPoMoFoUtil;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService {

    private final I8Request i8Request;
    private final FgOrglistService fgOrglistService;

    private static final String organizationFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"OCode\":\"test250409\",\"OName\":\"测试组织250409\",\"Bopomofo\":\"\",\"CodeValue\":\"\",\"OrgType\":\"Y\",\"IsActive\":\"1\",\"CloseOrgDt\":\"\",\"EbDt\":\"\",\"EeDt\":\"\",\"GfiYear\":\"\",\"GfiCboo\":\"\",\"RelatGfiCboo\":\"\",\"OrgOwner\":\"\",\"Financer\":\"\",\"Accounter\":\"\",\"IsInner\":0,\"AdminOrg\":0,\"OMemo\":\"\",\"attrcode14\":\"14\",\"attrcode18\":\"18\",\"attrcode23\":\"23\",\"attrcode30\":\"30\",\"attrcode45\":\"45\",\"attrcode46\":\"46\",\"OrgNatura\":\"\",\"FinanceCheck\":\"\",\"CheckDept\":\"\",\"RelProject\":\"\",\"BelongDept\":\"\",\"WeChatId\":\"\",\"IsColleague\":\"\",\"PhId\":\"\",\"OrgIndex\":\"\",\"OrgLabel\":\"\",\"IsWeChat\":\"0\",\"IsNfcShow\":\"0\",\"IsEnd\":\"0\",\"PersonFlg\":4,\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"\"}}}";
    private static final String enterpriseFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PersonFlg\":4,\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"PhId\":\"\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"\"}}}";
    private static final String deptOrganizationFormData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"OCode\":\"A001001.01\",\"OName\":\"集团行政管理中心\",\"Bopomofo\":\"\",\"CodeValue\":\"\",\"OrgType\":\"N\",\"IsActive\":\"1\",\"CloseOrgDt\":\"\",\"EbDt\":\"\",\"EeDt\":\"\",\"GfiYear\":\"\",\"GfiCboo\":\"\",\"RelatGfiCboo\":\"\",\"OrgOwner\":\"0\",\"Financer\":\"0\",\"Accounter\":\"0\",\"AdminOrg\":0,\"OMemo\":\"\",\"OrgNatura\":\"\",\"FinanceCheck\":0,\"CheckDept\":\"347181107000019\",\"RelProject\":\"0\",\"BelongDept\":0,\"WeChatId\":24,\"IsWeChat\":\"1\",\"IsColleague\":\"\",\"IsInner\":0,\"PhId\":\"347181107000019\",\"OrgIndex\":\"\",\"OrgLabel\":\"\",\"IsNfcShow\":\"0\",\"IsEnd\":\"0\",\"attrcode46\":\"0\",\"ParentOrgId\":\"\",\"EmpCode\":\"\",\"OTax\":\"\",\"DeptAttr\":\"\",\"key\":\"347181107000019\"}}}";
    private static final String deptEnterpriseformData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PersonFlg\":\"\",\"UnisocialCredit\":\"\",\"OrgCodeCert\":\"\",\"RegDt\":\"\",\"RegMoney\":\"\",\"Person\":\"\",\"RegionId\":\"\",\"EnterNatureId\":\"\",\"TradeTypeId\":\"\",\"OTax\":\"\",\"TaxPayerType\":\"\",\"OAddr\":\"\",\"FcNatural\":\"\",\"OZip\":\"\",\"ParentCompId\":\"\",\"OTel\":\"\",\"OFax\":\"\",\"PhId\":\"347181107000019\",\"NationId\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"OCfo\":\"\",\"key\":\"347181107000019\"}}}";


    @Override
    public I8ReturnModel saveOrgOrDept(OrgSyncRequest data) {
        I8ReturnModel saveResult = "Y".equals(data.getOrgType()) ? saveOrg(data) : saveDept(data);
        return saveResult;
    }

    public I8ReturnModel saveOrg(OrgSyncRequest data) {
        log.info("同步组织入参: {}", JSONObject.toJSONString(data));
        //uipLog.info("saveOrg", "同步组织入参", JSONObject.toJSONString(data));
        try {
            LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FgOrglist::getUserYyzzid, data.getUser_yyzzid())
                    .eq(FgOrglist::getOrgtype, "Y");
            List<FgOrglist> list = fgOrglistService.list(queryWrapper);
            if (list.isEmpty()) {
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();
                mapInfo.put("OCode", data.getOCode());
                mapInfo.put("OName", data.getOName());
                mapInfo.put("OrgType", data.getOrgType());
                mapInfo.put("user_yyzzid", data.getUser_yyzzid());
//                mapInfo.put("UnisocialCredit", data.getUnisocialCredit());

                //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                String parentOrg = data.getParentOrg();
                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getUserYyzzid, parentOrg)
                        .eq(FgOrglist::getOrgtype, "Y");
                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                if(!list1.isEmpty()) {
                    FgOrglist fgOrglist = list1.get(0);
                    mapInfo.put("ParentOrgId", fgOrglist.getPhid());
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
                urlParameters.add(new BasicNameValuePair("ng3_logid", "347181108000008"));

                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Org/Organization/save", urlParameters);
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("组织新增成功");
                }
            } else {
                LambdaUpdateWrapper<FgOrglist> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(FgOrglist::getUserYyzzid, data.getUser_yyzzid())
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

    public I8ReturnModel saveDept(OrgSyncRequest data) {
        log.info("同步部门入参: {}", JSONObject.toJSONString(data));
        try {
            LambdaQueryWrapper<FgOrglist> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FgOrglist::getUserYyzzid, data.getUser_yyzzid())
                    .eq(FgOrglist::getOrgtype, "N");
            List<FgOrglist> list = fgOrglistService.list(queryWrapper);
            if (list.isEmpty()) {
                List<NameValuePair> urlParameters = new ArrayList<>();
                HashMap<String, Object> mapInfo = new HashMap<>();
                mapInfo.put("OCode", data.getOCode());
                mapInfo.put("OName", data.getOName());
                mapInfo.put("OrgType", data.getOrgType());
                mapInfo.put("user_yyzzid", data.getUser_yyzzid());

                //TODO 需要确定parentOrg是否为ocode,视具体情况决定如何匹配我方数据库
                String parentOrg = data.getParentOrg();
                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getUserYyzzid, parentOrg)
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
                urlParameters.add(new BasicNameValuePair("ng3_logid", "347181108000008"));

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
                        .set(FgOrglist::getUserYyzzid, data.getUser_yyzzid());
                if (fgOrglistService.update(updateWrapper)) {
                    return I8ResultUtil.success("部门更新成功");
                }
            }
            return I8ResultUtil.error(data.getOCode() + "--" + data.getOName() + ", 该部门同步失败");
        } catch (Exception e) {
            return I8ResultUtil.error("部门同步失败 : " + e.getMessage());
        }
    }
}
