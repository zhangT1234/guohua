package com.newgrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.ProjectSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.ProjectTableModel;
import com.newgrand.domain.po.FgOrglist;
import com.newgrand.service.ProjectService;
import com.newgrand.service.ProjectTableService;
import com.newgrand.service.mp.FgOrglistService;
import com.newgrand.utils.i8util.GetPhIdHelper;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectServiceImpl implements ProjectService {
    private final I8Request i8Request;
    private final ProjectTableService projectTableService;
    private final GetPhIdHelper getPhIdHelper;
    private final FgOrglistService fgOrglistService;

    private static final String dmMain = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PcNo\":\"00000000000000202508001\",\"ProjectName\":\"新中大测试项目\",\"Ab\":\"\",\"Szfx\":\"1\",\"PhIdType\":\"215190319000002\",\"Stat\":\"sts\",\"StartDate\":\"2025-08-06\",\"EndDate\":\"2025-08-30\",\"CatPhId\":\"282190306000004\",\"ProjectManager\":\"\",\"LimitTime\":25,\"ApproxContractFc\":10,\"PhIdFiOcode\":\"282190306000004\",\"ProjectOrg\":\"418190308000014\",\"ManageMode\":\"0\",\"PhIdCompany\":\"108190318000006\",\"PhIdSgOrg\":\"108190318000006\",\"VirtualFlg\":\"4\",\"Descript\":\"\",\"GroupShare\":\"0\",\"PhIdScheme\":\"646190307000001\",\"user_bhzxm\":\"01\",\"user_yyid\":\"\",\"PhidBuildType\":\"\",\"NgRecordVer\":\"\",\"user_user_xmlx\":\"\",\"user_user_sfsj\":\"\",\"RecordManager\":\"\",\"ListFilterVal\":\"\",\"QueryField1Val\":\"\",\"QueryField2Val\":\"\",\"UIMultiConfigID\":\"\",\"RecordName\":\"新中大测试项目\",\"ManagerTel\":\"\",\"ShareOrg\":\"\",\"NGWriteSource\":\"\",\"Pc\":\"\",\"RiskFlag\":\"\",\"FactStartDt\":\"\",\"FactEndDt\":\"\",\"FactTime\":0,\"DutyOfficer\":\"\",\"ChkFlg\":\"0\",\"PhIdApp\":\"\",\"WfFlg\":\"0\",\"DaFlg\":\"0\",\"BillFlg\":\"1\",\"PhIdOri\":0,\"PhId\":\"0\",\"IsSynchronous\":0,\"Guid\":\"\",\"ImpInfo\":\"\",\"PhidSourceMId\":\"\",\"ItemResource\":\"\",\"Py\":\"XZDCSXM\",\"PointId\":\"\",\"Creator\":\"\",\"PhIdMultiCompany\":\"\",\"CatDept\":\"\",\"PhidTask\":\"\",\"OrgByProject\":\"0\",\"AsrFlg\":\"0\",\"CountryId\":\"1\",\"ProvinceId\":0,\"CityId\":0,\"RegionId\":0,\"ProjectAddress\":\"\",\"CurrType\":\"1\",\"CntAmtFc\":10,\"JobTax\":\"\",\"ManageArea\":\"\",\"JobPhone\":\"\",\"BuildingArea\":0,\"Msunit\":\"\",\"IsQuality\":\"\",\"IsSafety\":\"\",\"ProjectParentId\":\"\",\"CntNo\":\"\",\"ContractType\":\"\",\"ContractorDate\":\"\",\"ExchRate\":1,\"CntAmt\":10,\"IsClass\":\"\",\"PcClass\":\"\",\"BuildIsclass\":\"\",\"BuildClass\":\"\",\"ConRecorde\":\"\",\"WorkRecorde\":\"\",\"Deadline\":\"\",\"GSituation\":\"\",\"Longitude\":0,\"Latitude\":0,\"ImposeType\":\"2\",\"ImposeArea\":\"1\",\"PreImposeRate\":0,\"PermitDtB\":\"\",\"PhIdRatepayOcode\":\"\",\"PhIdTaxOcode\":\"\",\"IsWjz\":\"\",\"InvType\":\"\",\"InvAccount\":\"\",\"InvPassword\":\"\",\"IsRealEstate\":\"0\",\"LandPurchaseFund\":\"\",\"AbovegroundArea\":\"\",\"UndergroundArea\":\"\",\"ljkpje\":0,\"ljxxse\":0,\"ydkjx\":0,\"yjnzzs\":0,\"DecOrg\":\"\",\"PhIdTaxCenter\":\"\",\"Bank\":\"\",\"Bankaccount\":\"\",\"TaxRate\":0.03,\"CntBoqModel\":\"2\",\"BoqCtrlQuota\":\"2\",\"Boqtores\":\"0\",\"PhidYsfl\":\"\",\"IsToPn\":0,\"LbProjseries\":\"\",\"Tbmodel\":\"\",\"IsPcCostkz\":\"1\",\"UseDefaultPrc\":\"0\",\"RoleControl\":\"0\",\"ShowLeave\":\"0\",\"key\":\"0\"}}}";
    private static final String dmEmployee = "{\"table\":{\"key\":\"PhId\"}}";
    private static final String dmCorrect = "{\"table\":{\"key\":\"PhId\",\"newRow\":[{\"row\":{\"PhId\":\"\",\"Code\":\"01\",\"Pc\":\"\",\"TypeCode\":1,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"11\",\"PhidBsYs_EXName\":\"合同收入\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"05\",\"Pc\":\"\",\"TypeCode\":2,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"100\",\"PhidBsYs_EXName\":\"投标预算\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"06\",\"Pc\":\"\",\"TypeCode\":3,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"101\",\"PhidBsYs_EXName\":\"施工图预算\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"02\",\"Pc\":\"\",\"TypeCode\":4,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"12\",\"PhidBsYs_EXName\":\"目标成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"03\",\"Pc\":\"\",\"TypeCode\":5,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"13\",\"PhidBsYs_EXName\":\"责任成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"04\",\"Pc\":\"\",\"TypeCode\":6,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"14\",\"PhidBsYs_EXName\":\"计划成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"05\",\"Pc\":\"\",\"TypeCode\":7,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"363190507000001\",\"PhidBsYs_EXName\":\"计量导入\",\"PhidBsYs_EXName_Flag\":\"\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"06\",\"Pc\":\"\",\"TypeCode\":8,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"312210127000001\",\"PhidBsYs_EXName\":\"月度目标成本\",\"PhidBsYs_EXName_Flag\":\"\",\"key\":null}}]},\"isChanged\":true}";

    @Override
    public I8ReturnModel saveData(ProjectSyncRequest data) {
        try {
            List<NameValuePair> params = new ArrayList<>();
            LambdaQueryWrapper<ProjectTableModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProjectTableModel::getUserYyid, data.getUser_yyid());
            List<ProjectTableModel> list = projectTableService.list(queryWrapper);
            if (list.isEmpty()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("PcNo", data.getPcNo());
                map.put("ProjectName", data.getProjectName());
                map.put("RecordName", data.getProjectName());
                map.put("Szfx", data.getSzfx());
                map.put("ManageMode", data.getManageMode());
                map.put("StartDate", data.getStartDate());
                map.put("EndDate", data.getEndDate());
                map.put("user_bhzxm", data.getUser_bhzxm());
                map.put("ImposeType", data.getImposeType());
                map.put("user_yyid", data.getUser_yyid());
                map.put("CntAmtFc", data.getCntAmtFc());
                map.put("ApproxContractFc", data.getApproxContractFc());

                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getUserYyzzid, data.getCatPhIdEXName());
                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                if (list1.isEmpty()) {
                    throw new RuntimeException("未找到对应的组织");
                }
                String phidOcode = list1.get(0).getPhid();
                map.put("PhIdFiOcode", phidOcode);
                map.put("CatPhId", phidOcode);
                map.put("ProjectOrg", phidOcode);

                String phidCompany = getPhIdHelper.GetPhIdByCode("fg3_enterprise", "user_yyid", "C" + data.getPhIdCompany());
                map.put("PhIdCompany", phidCompany);

                String phidSgOrg = getPhIdHelper.GetPhIdByCode("fg3_enterprise", "user_yyid2", "S" + data.getPhIdSgOrg());
                map.put("PhIdSgOrg", phidSgOrg);

                String phidType = getPhIdHelper.GetPhIdByCode("wbs_type", "type_name", data.getPhIdTypeEXName());
                map.put("PhIdType", phidType);

                params.add(new BasicNameValuePair("dmEmployee", dmEmployee));

                String mstformData = I8Converter.SetField(dmMain, map);
                params.add(new BasicNameValuePair("dmMain", mstformData));
                params.add(new BasicNameValuePair("dmCompany", ""));
                params.add(new BasicNameValuePair("dmCorrect", dmCorrect));
                params.add(new BasicNameValuePair("dmWareHouse", ""));
                params.add(new BasicNameValuePair("dmDeliverName", ""));
                params.add(new BasicNameValuePair("dmProjectDangerset", ""));
                params.add(new BasicNameValuePair("dmPcmap", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("dmAccout", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("bustype", "ProjectTable"));
                params.add(new BasicNameValuePair("isContinue", "false"));
                params.add(new BasicNameValuePair("attchmentGuid", "0"));
                params.add(new BasicNameValuePair("ng3_logid", ""));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/PMS/PC/ProjectTable/Save", params);
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("项目-" + data.getPcNo() + "，" + data.getProjectName() + "，保存成功！");
                } else {
                    return I8ResultUtil.error("项目保存失败");
                }
            } else {
                //更新
                LambdaUpdateWrapper<ProjectTableModel> updateWrapper = new LambdaUpdateWrapper<>();
                LambdaQueryWrapper<FgOrglist> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(FgOrglist::getUserYyzzid, data.getCatPhIdEXName());
                List<FgOrglist> list1 = fgOrglistService.list(queryWrapper1);
                if (list1.isEmpty()) {
                    throw new RuntimeException("未找到对应的组织");
                }

                FgOrglist fgOrglist = list1.get(0);
                String phidOcode = fgOrglist.getPhid();

                String phidCompany = getPhIdHelper.GetPhIdByCode("fg3_enterprise", "user_yyid", "C" + data.getPhIdCompany());
                updateWrapper.eq(ProjectTableModel::getUserYyid, data.getUser_yyid());
                updateWrapper.set(ProjectTableModel::getPhidCompany, phidCompany)
                        .set(ProjectTableModel::getCatPhid, phidOcode)
                        .set(ProjectTableModel::getPhidFiOcode, phidOcode)
                        .set(ProjectTableModel::getProjectOrg, phidOcode)
                        .set(ProjectTableModel::getPcNo, data.getPcNo())
                        .set(ProjectTableModel::getProjectName, data.getProjectName())
                        .set(ProjectTableModel::getSzfx, data.getSzfx())
                        .set(ProjectTableModel::getManageMode, data.getManageMode())
                        .set(ProjectTableModel::getStartDate, data.getStartDate())
                        .set(ProjectTableModel::getEndDate, data.getEndDate())
                        .set(ProjectTableModel::getUserBhzxm, data.getUser_bhzxm())
                        .set(ProjectTableModel::getImposeType, data.getImposeType())
                        .set(ProjectTableModel::getCntAmtFc, data.getCntAmtFc())
                        .set(ProjectTableModel::getApproxContractFc, data.getApproxContractFc());
                projectTableService.update(updateWrapper);
                return I8ResultUtil.success("项目-" + data.getPcNo() + "，" + data.getProjectName() + "，更新成功！");
            }
        } catch (Exception ex) {
            return I8ResultUtil.error("项目保存失败：" + ex.getMessage());
        }
    }
}
