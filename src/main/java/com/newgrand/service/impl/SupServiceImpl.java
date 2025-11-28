package com.newgrand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.SupSyncRequest;
import com.newgrand.domain.model.Fg3Enterprise;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.service.Fg3EnterpriseService;
import com.newgrand.service.SupService;
import com.newgrand.utils.NGEncodeUtil;
import com.newgrand.utils.StringUtils;
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
public class SupServiceImpl implements SupService {
    private final I8Request i8Request;
    private final Fg3EnterpriseService fg3EnterpriseService;

    private static final String entpriseformDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"CompNo\":\"\",\"CompName\":\"\",\"PersonFlg\":0,\"SimpName\":\"\",\"SimpName2\":\"\",\"HelpCode\":\"\",\"OldName\":\"\",\"UnisocialCredit\":\"\",\"TaxNo\":\"\",\"CardType\":\"\",\"CardNo\":\"\",\"NationId\":1,\"ProvinceId\":\"\",\"CityId\":\"\",\"RegionId\":\"0\",\"TradeTypeId\":\"\",\"ParentCompId\":\"\",\"GroupShareFlg_tmp\":\"1\",\"Remarks\":\"\",\"IsTemp_EXName\":\"\",\"IsInner_EXName\":\"\",\"IsBlackList_EXName\":\"\",\"IcChangeDt\":\"\",\"PhId\":\"\",\"AuditFlg\":\"\",\"AuditDt\":\"\",\"AuditPsnId\":\"\",\"EstablishDate\":\"\",\"TaxPayerType\":\"\",\"TaxPayerName\":\"测试供应商\",\"TaxBankId\":\"\",\"TaxBankName\":\"\",\"TaxAccountNo\":\"\",\"TaxAddress\":\"\",\"TaxTelePhone\":\"\",\"RegMoney\":\"\",\"RegDt\":\"\",\"RegScope\":\"\",\"Person\":\"\",\"TradeGradeId\":\"\",\"EnterNatureId\":\"\",\"ScaleId\":\"\",\"Url\":\"\",\"Email\":\"\",\"IsTemp\":\"0\",\"IsInner\":\"0\",\"IsBlackList_tmp\":\"0\",\"CreateCustom_tmp\":\"0\",\"key\":\"\"}}}";
    private static final String supplyfileformDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"FsaleFlg\":1,\"SupplyRankId\":\"0\",\"DeptId\":\"\",\"EmpId\":\"\",\"OrgId\":\"282190306000004\",\"Creator\":\"418190308000002\",\"NgInsertDt\":\"\",\"AuditFlg\":\"\",\"Accstop\":\"\",\"AccstopBDt\":\"\",\"AccstopEndDt\":\"\",\"GfiCboo\":\"\",\"BidFlag\":0,\"ServiceArea\":\"\",\"ValidityDt\":\"\",\"user_xdlx\":\"\",\"EntId\":\"\",\"SuppClassId\":\"1000\",\"AuditPsnId\":\"\",\"AuditDt\":\"\",\"PhIdScheme\":\"\",\"ImpInfo\":\"\",\"ResourceTable\":\"\",\"PhIdResource\":\"\",\"PhId\":\"\",\"NgRecordVer\":\"\",\"GroupShareFlg\":\"1\",\"IsBlackList\":\"0\",\"CreateCustom\":\"0\",\"WfFlag\":\"\",\"VaryStatus\":\"\",\"Editor\":\"\",\"NgUpdateDt\":\"\",\"key\":\"\"}}}";
    private static final String suppCategoryGridDataStr = "{\"table\":{\"key\":\"PhId\",\"newRow\":[{\"row\":{\"id\":\"1\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"1\",\"CategoryNo\":\"010001\",\"CategoryName\":\"分包商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":0,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":true,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[{\"PhId\":0,\"EntId\":0,\"OrgId\":0,\"CategoryId\":11,\"PhIdSupplyRank\":0,\"PhIdSupplyRank_EXName\":\"\",\"Selected\":false,\"SelectedMain\":false,\"CategoryNo\":\"01000101\",\"CategoryName\":\"专业分包商\",\"SortId\":0,\"GroupShareFlg\":0,\"ParentId\":1,\"NgUpdateDt\":\"2025-08-07T17:36:36.6518228\",\"NgInsertDt\":\"2025-08-07T17:36:36.6518228\",\"NgRecordVer\":0,\"Editor\":0,\"Creator\":0,\"LeafSeted\":true,\"id\":\"11\",\"text\":\"专业分包商\",\"cls\":null,\"expanded\":true,\"children\":[],\"leaf\":true,\"hrefTarget\":null,\"myLevel\":2,\"allowDrag\":false,\"exparams\":null,\"customsort\":\"0\",\"iconCls\":\"no-icon\",\"disabled\":false},{\"PhId\":0,\"EntId\":0,\"OrgId\":0,\"CategoryId\":12,\"PhIdSupplyRank\":0,\"PhIdSupplyRank_EXName\":\"\",\"Selected\":false,\"SelectedMain\":false,\"CategoryNo\":\"01000102\",\"CategoryName\":\"劳务分包商\",\"SortId\":0,\"GroupShareFlg\":0,\"ParentId\":1,\"NgUpdateDt\":\"2025-08-07T17:36:36.6518238\",\"NgInsertDt\":\"2025-08-07T17:36:36.6518238\",\"NgRecordVer\":0,\"Editor\":0,\"Creator\":0,\"LeafSeted\":true,\"id\":\"12\",\"text\":\"劳务分包商\",\"cls\":null,\"expanded\":true,\"children\":[],\"leaf\":true,\"hrefTarget\":null,\"myLevel\":2,\"allowDrag\":false,\"exparams\":null,\"customsort\":\"0\",\"iconCls\":\"no-icon\",\"disabled\":false}],\"key\":null}},{\"row\":{\"id\":\"2\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"2\",\"CategoryNo\":\"010002\",\"CategoryName\":\"运输单位\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":1,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"3\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"3\",\"CategoryNo\":\"010003\",\"CategoryName\":\"设计院\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":2,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"4\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"4\",\"CategoryNo\":\"010004\",\"CategoryName\":\"监理单位\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":3,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"5\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"5\",\"CategoryNo\":\"010005\",\"CategoryName\":\"材料供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":4,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"6\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"6\",\"CategoryNo\":\"010006\",\"CategoryName\":\"其他供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":5,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"7\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"7\",\"CategoryNo\":\"010007\",\"CategoryName\":\"设备供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":6,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"8\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"8\",\"CategoryNo\":\"019999\",\"CategoryName\":\"物流公司\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":13,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"9\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"9\",\"CategoryNo\":\"010008\",\"CategoryName\":\"服务供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":7,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"10\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"10\",\"CategoryNo\":\"010009\",\"CategoryName\":\"劳务负责人\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":8,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"11\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"11\",\"CategoryNo\":\"01000101\",\"CategoryName\":\"专业分包商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"1\",\"index\":0,\"depth\":2,\"expanded\":false,\"expandable\":true,\"checked\":null,\"leaf\":true,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":true,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":false,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"12\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"12\",\"CategoryNo\":\"01000102\",\"CategoryName\":\"劳务分包商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"1\",\"index\":1,\"depth\":2,\"expanded\":false,\"expandable\":true,\"checked\":null,\"leaf\":true,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":true,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":false,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"1000\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"1000\",\"CategoryNo\":\"1\",\"CategoryName\":\"无类型\",\"SelectedMain\":true,\"Selected\":true,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":14,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":true,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000001\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000001\",\"CategoryNo\":\"010009\",\"CategoryName\":\"内部供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":9,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000002\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000002\",\"CategoryNo\":\"010010\",\"CategoryName\":\"标准供应商（有信用代码）\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":10,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220222000003\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220222000003\",\"CategoryNo\":\"010011\",\"CategoryName\":\"非标供应商（无信用代码）\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":11,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}},{\"row\":{\"id\":\"312220729000001\",\"PhId\":\"0\",\"EntId\":\"0\",\"OrgId\":\"0\",\"CategoryId\":\"312220729000001\",\"CategoryNo\":\"010012\",\"CategoryName\":\"兴达供应商\",\"SelectedMain\":false,\"Selected\":false,\"Creator\":\"0\",\"Editor\":\"0\",\"NgInsertDt\":\"2025-8-07 17:36:36\",\"NgUpdateDt\":\"2025-8-07 17:36:36\",\"NgRecordVer\":0,\"GroupShareFlg\":0,\"PhIdSupplyRank\":\"0\",\"PhIdSupplyRank_EXName\":\"\",\"parentId\":\"root\",\"index\":12,\"depth\":1,\"expanded\":true,\"expandable\":true,\"checked\":null,\"leaf\":false,\"cls\":null,\"iconCls\":\"no-icon\",\"icon\":\"\",\"root\":false,\"isLast\":false,\"isFirst\":false,\"allowDrop\":true,\"allowDrag\":false,\"loaded\":true,\"loading\":false,\"href\":\"\",\"hrefTarget\":null,\"qtip\":\"\",\"qtitle\":\"\",\"qshowDelay\":0,\"children\":[],\"key\":null}}]},\"isChanged\":true}";
    private static final String supplysettleinfoDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"BillToId\":\"\",\"PayWayId\":\"\",\"FcId\":\"1\",\"BankId\":\"\",\"AccountNo\":\"\",\"DiscType\":\"\",\"DiscRate\":\"\",\"AccountName\":\"\",\"ProvinceId\":\"\",\"CityId\":\"\",\"RegionId\":\"\",\"CreditRankId\":\"\",\"CreditDays\":\"\",\"CreditNum\":\"\",\"PayDay\":\"\",\"PayAcctId\":\"\",\"PrepayAcctId\":\"\",\"PurAcctId\":\"\",\"InAcctId\":\"\",\"PhId\":\"\",\"key\":\"\"}}}";
    private static final String supplybusiinfoDataStr = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"OrderCyc\":\"\",\"DelDay\":\"\",\"BackPeriod\":\"\",\"BackRate\":\"\",\"PhId\":\"\",\"GoodsToId\":\"\",\"ShipTypeId\":\"\",\"SupitemBarCodeStart\":\"\",\"SupitemBarCodeLength\":\"\",\"key\":\"\"}}}";

    @Override
    public I8ReturnModel saveData(SupSyncRequest data) {
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
            queryWrapper.eq(Fg3Enterprise::getUserYyid, "S" + data.getUser_yyid());
            if("1".equals(data.getPersonFlg())) {
                queryWrapper.or().eq(Fg3Enterprise::getCardNo, data.getCardNo());
            }
            if(!"1".equals(data.getPersonFlg()) && !"4".equals(data.getPersonFlg())) {
                queryWrapper.or().eq(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit());
            }
            List<Fg3Enterprise> list = fg3EnterpriseService.list(queryWrapper);
            if (list.isEmpty()) {
                HashMap<String, Object> entpriseMap = new HashMap<>();
                HashMap<String, Object> settleInfoMap = new HashMap<>();

                entpriseMap.put("CompNo", data.getCompNo());
                entpriseMap.put("CompName", data.getCompName());
                entpriseMap.put("PersonFlg", data.getPersonFlg());
                entpriseMap.put("UnisocialCredit", data.getUnisocialCredit());
                entpriseMap.put("user_yyid2", "S" + data.getUser_yyid());
                entpriseMap.put("CardType", data.getCardType());
                entpriseMap.put("CardNo", data.getCardNo());

                String entpriseformData = I8Converter.SetField(entpriseformDataStr, entpriseMap);

                String settleInfoDataStr = I8Converter.SetField(supplysettleinfoDataStr, settleInfoMap);

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("entpriseformData", entpriseformData));
                params.add(new BasicNameValuePair("supplyfileformData", supplyfileformDataStr));
                params.add(new BasicNameValuePair("addressgridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("linkmangridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("suppCategoryGridData", suppCategoryGridDataStr));
                params.add(new BasicNameValuePair("suppItemGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("cardInfoGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("qualificationGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("supplysettleinfoData", settleInfoDataStr));
                params.add(new BasicNameValuePair("supplybusiinfoData", supplybusiinfoDataStr));
                params.add(new BasicNameValuePair("unitaccountData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("parentcompData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("suppEvaluateGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("laborSubContractorGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("busguid", ""));
                params.add(new BasicNameValuePair("puic_changeid", ""));
                params.add(new BasicNameValuePair("puic_changeresult", ""));
                params.add(new BasicNameValuePair("puic_changesuggest", ""));
                params.add(new BasicNameValuePair("empGridData", "{\"table\":{\"key\":\"PhId\"}}"));
                params.add(new BasicNameValuePair("refType", ""));
                params.add(new BasicNameValuePair("refId", "0"));
                params.add(new BasicNameValuePair("ocrguid", ""));
                params.add(new BasicNameValuePair("operatetype", ""));
                params.add(new BasicNameValuePair("ng3_logid", ""));

                HashMap<String, Object> headerMap = new HashMap<>();
                headerMap.put("Data-Encoded-Fields", "[\"entpriseformData\",\"addressgridData\",\"linkmangridData\",\"cardInfoGridData\",\"supplysettleinfoData\",\"unitaccountData\"]");
                headerMap.put("Data-Is-Encoded", "true");

                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/DMC/Enterprise/SupplyFile/save", params, headerMap);

                System.out.println("请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("供应商-" + data.getCompNo() + "，" + data.getCompName() + "，保存成功！");
                } else {
                    return I8ResultUtil.error("供应商保存失败");
                }
            } else {
                //更新供应商信息
                LambdaUpdateWrapper<Fg3Enterprise> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Fg3Enterprise::getUserYyid2, "S" + data.getUser_yyid());
                if("1".equals(data.getPersonFlg())) {
                    updateWrapper.or().eq(Fg3Enterprise::getCardNo, data.getCardNo());
                }
                if(!"1".equals(data.getPersonFlg()) && !"4".equals(data.getPersonFlg())) {
                    updateWrapper.or().eq(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit());
                }
                updateWrapper.set(Fg3Enterprise::getCompName, data.getCompName())
                        .set(Fg3Enterprise::getUserYyid2, "S" + data.getUser_yyid())
                        .set(Fg3Enterprise::getPersonFlg, data.getPersonFlg())
                        .set(Fg3Enterprise::getCardType, data.getCardType())
                        .set(Fg3Enterprise::getCardNo, data.getCardNo())
                        .set(Fg3Enterprise::getUnisocialCredit, data.getUnisocialCredit());
                fg3EnterpriseService.update(updateWrapper);
            }
            return I8ResultUtil.success("供应商账户-" + data.getCompNo() + "，" + data.getCompName() + "，保存成功！");
        } catch (Exception ex) {
            return I8ResultUtil.error("供应商账户保存失败：" + ex.getMessage());
        }
    }
}
