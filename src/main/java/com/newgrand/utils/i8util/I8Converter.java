package com.newgrand.utils.i8util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class I8Converter {

    /**
     * 功能描述: 设置前端form表单的字段值
     *
     * @Param: [baseData, map]
     * @Return: java.lang.String
     */
    public static String SetField(String baseData, HashMap<String, Object> map) {
        JSONObject root = JSON.parseObject(baseData);
        JSONObject form = root.getJSONObject("form").getJSONObject("newRow");
        if (form == null) {
            form = root.getJSONObject("form").getJSONObject("modifiedRow");
        }
        Iterator var4 = map.keySet().iterator();
        while (var4.hasNext()) {
            String key = (String) var4.next();
            form.put(key, map.get(key));
        }
        return root.toJSONString();
    }

    /**
     * @param baseData
     * @param map
     * @return
     */
    public static String SetFieldModify(String baseData, HashMap<String, Object> map) {
        JSONObject root = JSON.parseObject(baseData);
        JSONObject form = root.getJSONObject("form").getJSONObject("modifiedRow");
        for (String key : map.keySet()) {
            form.put(key, map.get(key));
        }
        return root.toJSONString();
    }

    /**
     * @param rowDataTmp
     * @param list
     * @return
     * @throws Exception
     */
    public static String SetTableRow(String rowDataTmp, ArrayList<HashMap<String, Object>> list) throws Exception {
        if (list.size() == 0) return rowDataTmp;
        JSONArray rvarr = new JSONArray();
        JSONArray jaa = JSON.parseArray(rowDataTmp);
        if (jaa.size() != list.size()) throw new Exception("list长度与模板不相等");
        for (int i = 0; i < list.size(); i++) {
            JSONObject jo = jaa.getJSONObject(i).getJSONObject("row");
            for (String key : list.get(i).keySet()) {
                jo.put(key, list.get(i).get(key));
            }
            JSONObject joo = new JSONObject();
            joo.put("row", jo);
            rvarr.add(joo);
        }
        return "{\"table\":{\"key\":\"PhId\",\"newRow\":" + rvarr + "},\"isChanged\":true}";
    }

    /**
     * @param rowDataTmp
     * @param list
     * @return
     * @throws Exception
     */
    public static String SetTableRowEdit(String rowDataTmp, ArrayList<HashMap<String, Object>> list) throws Exception {
        if (list.size() == 0) return rowDataTmp;
        JSONArray rvarr = new JSONArray();
        JSONArray jaa = JSON.parseArray(rowDataTmp);
        if (jaa.size() != list.size()) throw new Exception("list长度与模板不相等");
        for (int i = 0; i < list.size(); i++) {
            JSONObject jo = jaa.getJSONObject(i).getJSONObject("row");
            for (String key : list.get(i).keySet()) {
                jo.put(key, list.get(i).get(key));
            }
            JSONObject joo = new JSONObject();
            joo.put("row", jo);
            rvarr.add(joo);
        }
        return "{\"table\":{\"key\":\"PhId\",\"modifiedRow\":" + rvarr.toString() + "},\"isChanged\":true}";
    }
}
