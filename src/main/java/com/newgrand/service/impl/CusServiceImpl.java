package com.newgrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.CusSyncRequest;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.CusService;
import com.newgrand.service.Fg3EnterpriseService;
import com.newgrand.utils.NGEncodeUtil;
import com.newgrand.utils.StringUtils;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CusServiceImpl implements CusService {
    private final I8Request i8Request;
    private final Fg3EnterpriseService fg3EnterpriseService;

    private static final String entpriseformData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"CompNo\":\"\",\"CompName\":\"\",\"PersonFlg\":0,\"SimpName\":\"\",\"SimpName2\":\"\",\"HelpCode\":\"\",\"OldName\":\"\",\"UnisocialCredit\":\"\",\"TaxNo\":\"\",\"CardType\":\"\",\"CardNo\":\"\",\"NationId\":1,\"ProvinceId\":\"\",\"CityId\":\"\",\"RegionId\":\"0\",\"TradeTypeId\":\"\",\"ParentCompId\":\"\",\"GroupShareFlg_tmp\":\"0\",\"Remarks\":\"\",\"IsBlackList_EXName\":\"\",\"IcChangeDt\":\"\",\"PhId\":\"\",\"AuditFlg\":\"\",\"AuditDt\":\"\",\"AuditPsnId\":\"\",\"FromType\":\"\",\"EstablishDate\":\"\",\"TaxPayerType\":\"\",\"TaxPayerName\":\"新中大测试客户\",\"TaxBankId\":\"\",\"TaxBankName\":\"\",\"TaxAccountNo\":\"\",\"TaxAddress\":\"\",\"TaxTelePhone\":\"\",\"RegMoney\":\"\",\"RegDt\":\"\",\"RegScope\":\"\",\"Person\":\"\",\"TradeGradeId\":\"\",\"EnterNatureId\":\"\",\"ScaleId\":\"\",\"Url\":\"\",\"Email\":\"\",\"IsTemp\":\"0\",\"IsInner\":\"0\",\"IsBlackList_tmp\":\"0\",\"IsBalance_tmp\":\"0\",\"IsDirect_tmp\":\"0\",\"IsDealer_tmp\":\"0\",\"CreateSupply_tmp\":\"0\",\"key\":\"\"}}}";
    private static final String customfileformData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"SourceId\":\"\",\"CustClassId\":\"1000\",\"CustomRankId\":\"\",\"ImportDegreeId\":\"\",\"DeptId\":\"\",\"EmpId\":\"\",\"OrgId\":\"282190306000004\",\"Creator\":\"418190308000002\",\"NgInsertDt\":\"\",\"Accstop\":\"\",\"AccstopBDt\":\"\",\"AccstopEndDt\":\"\",\"ChannelTypeId\":\"\",\"ChannelRankId\":\"\",\"GfiCboo\":\"\",\"AuditFlg\":\"\",\"BranchType\":\"\",\"BranchProvince\":\"\",\"BranchCity\":\"\",\"EntId\":\"\",\"AuditDt\":\"\",\"AuditPsnId\":\"\",\"PhIdScheme\":\"\",\"ImpInfo\":\"\",\"ResourceTable\":\"\",\"PhIdResource\":\"\",\"PhId\":\"\",\"NgRecordVer\":\"\",\"CustomAttr\":\"\",\"GroupShareFlg\":\"0\",\"IsBalance\":\"0\",\"IsDirect\":\"0\",\"IsDealer\":\"0\",\"CreateSupply\":\"0\",\"IsBlackList\":\"0\",\"VaryStatus\":\"\",\"WfFlag\":\"\",\"key\":\"\"}}}";
    private static final String customsettleinfoData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"BilltoId\":\"\",\"PayWayId\":\"\",\"FcId\":\"1\",\"BankId\":\"\",\"AccountNo\":\"\",\"DiscType\":\"\",\"DiscRate\":\"\",\"AccountName\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"RegionId\":\"\",\"FinTypeId\":\"\",\"CtaxRate\":\"\",\"GatherDays\":\"\",\"Rate\":\"\",\"CreditRankId\":\"\",\"CreditDays\":\"\",\"CreditNum\":\"\",\"IsConCredit\":\"0\",\"InvoiceTypeId\":\"\",\"PrerecAcctId\":\"\",\"RecAcctId\":\"\",\"SaleAcctId\":\"\",\"InAcctId\":\"\",\"PhId\":\"\",\"key\":\"\"}}}";
    private static final String custombusiinfoData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"SaleTypeId\":\"\",\"OrderTypeId\":\"\",\"BackPeriod\":\"\",\"BackRate\":\"\",\"GoodstoId\":\"\",\"ShipTypeId\":\"\",\"RoadNoId\":\"\",\"RelWhouseId\":\"\",\"RelCompId\":\"\",\"PhId\":\"\",\"key\":\"\"}}}";
    private static final String custCategoryGridData = "{\"table\":{\"key\":\"PhId\",\"newRow\":[{\"row\":{\"id\":\"1\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"1\",\"CategoryNo\":\"015001\",\"CategoryName\":\"建设单位\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":4,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"2\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"2\",\"CategoryNo\":\"015002\",\"CategoryName\":\"其他客户\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":5,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"3\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"3\",\"CategoryNo\":\"015003\",\"CategoryName\":\"发包商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":6,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"200\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"200\",\"CategoryNo\":\"ZBFZJG\",\"CategoryName\":\"总部/分支机构\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":9,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":true,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"1000\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"1000\",\"CategoryNo\":\"1\",\"CategoryName\":\"无类型\",\"SelectedMain\":true,\"Selected\":true,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":7,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220217000001\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220217000001\",\"CategoryNo\":\"001\",\"CategoryName\":\"项目名称\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":0,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":true,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000001\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000001\",\"CategoryNo\":\"003\",\"CategoryName\":\"内部客户\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":1,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000002\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000002\",\"CategoryNo\":\"004\",\"CategoryName\":\"标准客户（有信用代码）\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":2,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000003\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000003\",\"CategoryNo\":\"005\",\"CategoryName\":\"非标客户（无信用代码）\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":3,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312190520000001\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312190520000001\",\"CategoryNo\":\"2\",\"CategoryName\":\"拌和中心客户\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:45:30\",\"NgUpdateDt\":\"2025-8-07 17:45:30\",\"NgRecordVer\":0,\"parentId\":\"root\",\"index\":8,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}}]},\"isChanged\":true}";

    @Override
    public I8ReturnModel saveData(CusSyncRequest data) {
        try {
            if("1".equals(data.getPersonFlg()) && (StringUtils.isEmpty(data.getCardType()) || StringUtils.isEmpty(data.getCardNo()))) {
                return I8ResultUtil.error("当单位属性为个人时证件类型跟证件编号不能为空");
            }
            if(!"1".equals(data.getPersonFlg()) && !"4".equals(data.getPersonFlg())) {
                if(data.getUnisocialCredit() == null || "".equals(data.getUnisocialCredit())) {
                    return I8ResultUtil.error("统一社会信用证号不能为空");
                }
            }
            LambdaQueryWrapper<Fg3Enterprise> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Fg3Enterprise::getUserYyid, "C" + data.getUser_yyid());
            if("1".equals(data.getPersonFlg())) {
                queryWrapper.or().eq(Fg3Enterprise::getCardNo, data.getCardNo());
            }
            if(!"1".equals(data.getPersonFlg()) && !"4".equals(data.getPersonFlg())) {
                queryWrapper.or().eq(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit());
            }
            List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
            if (list.isEmpty()) {
                //新增
                HashMap<String, Object> custMap = new HashMap<>();
                custMap.put("CompNo", data.getCompNo());
                custMap.put("CompName", data.getCompName());
                custMap.put("PersonFlg", data.getPersonFlg());
                custMap.put("UnisocialCredit", data.getUnisocialCredit());
                custMap.put("user_yyid", "C" + data.getUser_yyid());
                custMap.put("CardType", data.getCardType());
                custMap.put("CardNo", data.getCardNo());

                String entpriseformDataStr = I8Converter.SetField(entpriseformData, custMap);

                HashMap<String, Object> infoMap = new HashMap<>();
                String settleInfoDataStr = I8Converter.SetField(customsettleinfoData, infoMap);

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("entpriseformData", entpriseformDataStr));
                params.add(new BasicNameValuePair("customfileformData", customfileformData));
                params.add(new BasicNameValuePair("addressgridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("linkmangridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("cardinfoData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("qualificationData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("customsettleinfoData", settleInfoDataStr));
                params.add(new BasicNameValuePair("custombusiinfoData", custombusiinfoData));
                params.add(new BasicNameValuePair("unitaccountData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("parentcompData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("customItemGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("custCategoryGridData", custCategoryGridData));
                params.add(new BasicNameValuePair("busguid", "a5be2ffb-3f57-0d7d-e064-98396d328371"));
                params.add(new BasicNameValuePair("operateType", ""));
                params.add(new BasicNameValuePair("empGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("refType", ""));
                params.add(new BasicNameValuePair("refId", "0"));
                params.add(new BasicNameValuePair("operatetype", ""));
                params.add(new BasicNameValuePair("ng3_logid", ""));

                HashMap<String, Object> headerMap = new HashMap<>();
                headerMap.put("Data-Encoded-Fields", "[\"entpriseformData\",\"addressgridData\",\"linkmangridData\",\"cardinfoData\",\"customsettleinfoData\",\"unitaccountData\"]");
                headerMap.put("Data-Is-Encoded", "true");
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Enterprise/CustomFile/save", params, headerMap);

                System.out.println("请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("客户-" + data.getCompNo() + "，" + data.getCompName() + "，保存成功！");
                } else {
                    return I8ResultUtil.error("客户保存失败");
                }
            } else {
                //更新
                LambdaUpdateWrapper<Fg3Enterprise> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Fg3Enterprise::getUserYyid, "C" + data.getUser_yyid());
                if("1".equals(data.getPersonFlg())) {
                    updateWrapper.or().eq(Fg3Enterprise::getCardNo, data.getCardNo());
                }
                if(!"1".equals(data.getPersonFlg()) && !"4".equals(data.getPersonFlg())) {
                    updateWrapper.or().eq(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit());
                }
                updateWrapper.set(Fg3Enterprise::getUserYyid, "C" + data.getUser_yyid())
                        .set(Fg3Enterprise::getCompName, data.getCompName())
                        .set(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit())
                        .set(Fg3Enterprise::getPersonFlg, data.getPersonFlg())
                        .set(Fg3Enterprise::getCardType, data.getCardType())
                        .set(Fg3Enterprise::getCardNo, data.getCardNo());
                boolean update = fg3EnterpriseService.update(updateWrapper);
                if (update) {
                    return I8ResultUtil.success("客户-" + data.getCompNo() + "，" + data.getCompName() + "，保存成功！");
                } else {
                    return I8ResultUtil.error("更新失败");
                }
            }

        } catch (Exception ex) {
            return I8ResultUtil.error("客户保存失败：" + ex.getMessage());
        }
    }
}
