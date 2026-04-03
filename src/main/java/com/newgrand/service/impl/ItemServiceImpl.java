package com.newgrand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.newgrand.domain.dto.ItemDataRequest;
import com.newgrand.domain.dto.ItemResRequest;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.domain.model.ItemData;
import com.newgrand.domain.model.ItemRes;
import com.newgrand.service.ItemDataService;
import com.newgrand.service.ItemResService;
import com.newgrand.service.ItemService;
import com.newgrand.utils.StringUtils;
import com.newgrand.utils.i8util.I8Converter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.I8ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private I8Request i8Request;
    @Autowired
    public ItemResService itemResService;
    @Autowired
    public ItemDataService itemDataService;

    private static final String mstformData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"ThisCode\":\"0005\",\"Ffid\":\"2\",\"Code\":\"0005\",\"Name\":\"设备11\",\"QtyCtrl\":1,\"AmtCtrl\":1,\"PhidMsunit\":\"\",\"PhidBuyer\":\"\",\"Taxrate\":0,\"PhidItemPropGrp\":\"\",\"ResProp\":1,\"ResourceType\":\"3\",\"PhId\":\"\",\"Ab\":\"\",\"ParentCode\":\"\",\"ParentPhid\":\"\",\"ExternId\":\"\",\"ExternPushType\":\"\",\"ExternPid\":\"\",\"IsEquipment\":\"0\",\"IsRevolve\":\"0\",\"IsFarmProduce\":\"0\",\"key\":\"\"}}}";

    private static final String mstformData1 = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"ItemNo\":\"20251210-0001\",\"ItemName\":\"设备2025\",\"PhidResbs\":\"443000000000001\",\"ResourceType\":\"3\",\"Ab\":\"SB2025\",\"ItemNameE\":\"\",\"CheckType\":3,\"PhidMsunit\":\"\",\"Spec\":\"\",\"SpecE\":\"\",\"Ffid\":\"2\",\"BarCode\":\"\",\"PhidItemPropGrp\":\"0\",\"PlanPrc\":\"\",\"Brand\":\"\",\"PurLead\":\"\",\"ResProp\":1,\"CommodityName\":\"\",\"PositiveDev\":0,\"NegativeDev\":0,\"Remarks\":\"\",\"RefCbsIds\":\"\",\"PhId\":\"\",\"AsrFlg\":\"\",\"CurOrgId\":\"114190218000001\",\"AuditFlg\":\"\",\"PhidAuditpsn\":\"\",\"ResBsCode\":\"0005\",\"WfFlg\":\"\",\"Creator\":\"\",\"ExternId\":\"\",\"ExternPushType\":\"\",\"PushExternState\":\"\",\"PhidNonmsunit\":\"\",\"CostType\":\"\",\"key\":\"\"}}}";

    private static final String psresbaseData = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PrcAd\":\"\",\"Sacle\":\"\",\"MainMat\":\"\",\"QtyCtrl\":1,\"PrcCtrl\":1,\"AmtCtrl\":1,\"AmtRg\":\"\",\"AmtRes\":\"\",\"AmtMac\":\"\",\"IsPub\":1,\"PhId\":\"\",\"PhidItemdata\":\"\",\"IsBat\":\"0\",\"IsModel\":\"0\",\"IsStop\":\"0\",\"IsEquipment\":\"0\",\"IsFarmProduce\":\"0\",\"TransportItem\":\"0\",\"key\":\"\"}}}";

    /**
     * 同步资源分类
     * @param data
     * @return
     */
    @Override
    public I8ReturnModel saveItemResData(ItemResRequest data) {
        try {
            if((StringUtils.isEmpty(data.getCode()) || StringUtils.isEmpty(data.getName()))) {
                return I8ResultUtil.error("资源分类编码和名称不能为空");
            }
            LambdaQueryWrapper<ItemRes> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ItemRes::getCode, data.getCode());
            List<ItemRes> list = itemResService.list(queryWrapper);
            if (list.isEmpty()) {
                //新增
                HashMap<String, Object> custMap = new HashMap<>();
                custMap.put("Code", data.getCode());
                custMap.put("ThisCode", data.getCode());
                custMap.put("Name", data.getName());
                custMap.put("Ffid", data.getFid());
                custMap.put("ResourceType", data.getResourceType());

                if (StringUtils.isNotEmpty(data.getParentCode())) {
                    LambdaQueryWrapper<ItemRes> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(ItemRes::getCode, data.getParentCode());
                    List<ItemRes> pList = itemResService.list(queryWrapper1);
                    if (CollectionUtil.isNotEmpty(pList)) {
                        custMap.put("ParentCode", pList.get(0).getCode());
                        custMap.put("ParentPhid", pList.get(0).getPhid());
                    }
                }
                String mstformDataStr = I8Converter.SetField(mstformData, custMap);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("mstformData", mstformDataStr));
                params.add(new BasicNameValuePair("isContinue", "false"));
                params.add(new BasicNameValuePair("attchmentGuid", "0"));
                params.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/PMS/BasicData/ResBs/Save", params);
                System.out.println("请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("资源分类保存成功");
                } else {
                    return I8ResultUtil.error("资源分类保存失败");
                }
            } else {
                //更新
                LambdaUpdateWrapper<ItemRes> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ItemRes::getCode, data.getCode());
                updateWrapper.set(ItemRes::getName, data.getName());
                boolean update = itemResService.update(updateWrapper);
                if (update) {
                    return I8ResultUtil.success("资源分类保存成功！");
                } else {
                    return I8ResultUtil.error("资源分类更新失败");
                }
            }
        } catch (Exception ex) {
            return I8ResultUtil.error("资源分类保存失败：" + ex.getMessage());
        }
    }

    /**
     * 同步资源主文件
     * @param data
     * @return
     */
    @Override
    public I8ReturnModel saveItemData(ItemDataRequest data) {
        try {
            if((StringUtils.isEmpty(data.getItemNo()) || StringUtils.isEmpty(data.getItemName()))) {
                return I8ResultUtil.error("资源主文件编码和名称不能为空");
            }
            //有没有资源分类
            if (data.getItemResRequest()!=null) {
                try {
                    if ((StringUtils.isNotEmpty(data.getItemResRequest().getCode()) || StringUtils.isNotEmpty(data.getItemResRequest().getName()))) {
                        ItemResRequest itemResRequest = data.getItemResRequest();
                        LambdaQueryWrapper<ItemRes> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(ItemRes::getUserOfsid, itemResRequest.getUserOfsid());
                        List<ItemRes> list = itemResService.list(queryWrapper);
                        if (list.isEmpty()) {
                          //新增
                          HashMap<String, Object> custMap = new HashMap<>();
                          custMap.put("Code", itemResRequest.getCode());
                          custMap.put("ThisCode", itemResRequest.getCode());
                          custMap.put("Name", itemResRequest.getName());
                          custMap.put("Ffid", data.getFid());
                          custMap.put("ResourceType", data.getResourceType());
                          custMap.put("user_ofsid", itemResRequest.getUserOfsid());

                          if (StringUtils.isNotEmpty(itemResRequest.getParentCode())) {
                            LambdaQueryWrapper<ItemRes> queryWrapper1 = new LambdaQueryWrapper<>();
                            queryWrapper1.eq(ItemRes::getUserOfsid, itemResRequest.getParentCode());
                            List<ItemRes> pList = itemResService.list(queryWrapper1);
                            if (CollectionUtil.isNotEmpty(pList)) {
                                custMap.put("ParentCode", pList.get(0).getCode());
                                custMap.put("ParentPhid", pList.get(0).getPhid());
                            }
                           }
                           String mstformDataStr = I8Converter.SetField(mstformData, custMap);
                           List<NameValuePair> params = new ArrayList<>();
                           params.add(new BasicNameValuePair("mstformData", mstformDataStr));
                           params.add(new BasicNameValuePair("isContinue", "false"));
                           params.add(new BasicNameValuePair("attchmentGuid", "0"));
                           params.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                           I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/PMS/BasicData/ResBs/Save", params);
                           log.info("资源分类请求返回：" + i8ReturnModel.toString());
                           if (i8ReturnModel.getIsOk()) {
                               log.info("资源分类保存成功");
                           } else {
                               log.info("资源分类保存失败");
                           }
                        } else {
                           //更新
                           LambdaUpdateWrapper<ItemRes> updateWrapper = new LambdaUpdateWrapper<>();
                           updateWrapper.eq(ItemRes::getCode, data.getItemResRequest().getCode());
                           updateWrapper.set(ItemRes::getName, data.getItemResRequest().getName());
                           boolean update = itemResService.update(updateWrapper);
                           if (update) {
                               log.info("资源分类保存成功！");
                           } else {
                               log.info("资源分类更新失败");
                           }
                        }
                    }
                } catch (Exception ex) {
                       log.info("资源分类保存失败：" + ex.getMessage());
                }
            }
            //资源主文件同步
            LambdaQueryWrapper<ItemData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ItemData::getUserOfsid, data.getUserOfsid());
            List<ItemData> list = itemDataService.list(queryWrapper);
            if (list.isEmpty()) {
                //新增
                HashMap<String, Object> custMap = new HashMap<>();
                custMap.put("ItemNo", data.getItemNo());
                custMap.put("ItemName", data.getItemName());
                custMap.put("Ffid", data.getFid());
                custMap.put("ResourceType", data.getResourceType());
                custMap.put("user_ofsid", data.getUserOfsid());

                if (StringUtils.isNotEmpty(data.getResCode())) {
                    LambdaQueryWrapper<ItemRes> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(ItemRes::getCode, data.getResCode());
                    List<ItemRes> pList = itemResService.list(queryWrapper1);
                    if (CollectionUtil.isNotEmpty(pList)) {
                        custMap.put("PhidResbs", pList.get(0).getPhid());
                    }
                }
                String itemDataStr = I8Converter.SetField(mstformData1, custMap);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("mstformData", itemDataStr));
                params.add(new BasicNameValuePair("psresbaseData", psresbaseData));
                params.add(new BasicNameValuePair("itemDataForWorkFlow", "0"));
                params.add(new BasicNameValuePair("itemdataRepeatCheck", "3"));
                params.add(new BasicNameValuePair("ng3_logid", "614210413000001"));
                I8ReturnModel i8ReturnModel = i8Request.PostFormSync("/PMS/BasicData/ItemData/Save", params);
                log.info("资源主文件请求返回：" + i8ReturnModel.toString());
                if (i8ReturnModel.getIsOk()) {
                    return I8ResultUtil.success("资源主文件保存成功");
                } else {
                    return I8ResultUtil.error("资源主文件保存失败");
                }
            } else {
                //更新
                LambdaUpdateWrapper<ItemData> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ItemData::getUserOfsid, data.getUserOfsid())
                        .set(ItemData::getItemName, data.getItemName());
                boolean update = itemDataService.update(updateWrapper);
                if (update) {
                    return I8ResultUtil.success("资源主文件保存成功！");
                } else {
                    return I8ResultUtil.error("资源主文件更新失败");
                }
            }
        } catch (Exception ex) {
            return I8ResultUtil.error("资源主文件保存失败：" + ex.getMessage());
        }
    }

}
