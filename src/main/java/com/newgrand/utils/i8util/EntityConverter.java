package com.newgrand.utils.i8util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class EntityConverter {

    public String SetFieldTable(String baseData, HashMap<String, Object> map) {
        JSONObject root = JSON.parseObject(baseData);
        JSONArray jsonArray = root.getJSONObject("table").getJSONArray("newRow");
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        JSONObject form = jsonObject.getJSONObject("row");
        for (String key : map.keySet()) {
            form.put(key, map.get(key));
        }
        return root.toJSONString();
    }

    public String SetFieldForm(String baseData, HashMap<String, Object> map) {
        JSONObject root = JSON.parseObject(baseData);
        JSONObject form = root.getJSONObject("form").getJSONObject("newRow");
        if (form == null) form = root.getJSONObject("form").getJSONObject("modifiedRow");
        for (String key : map.keySet()) {
            form.put(key, map.get(key));
        }
        return root.toJSONString();
    }

    public String SetTableRow(String rowDataTmp, ArrayList<HashMap<String, Object>> list) throws Exception {
        if (list.size() == 0) return rowDataTmp;
        JSONArray rvarr = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONArray jaa = JSON.parseArray(rowDataTmp);
            JSONObject jo = jaa.getJSONObject(0).getJSONObject("row");
            for (String key : list.get(i).keySet()) {
                jo.put(key, list.get(i).get(key));
            }
            JSONObject joo = new JSONObject();
            joo.put("row", jo);
            rvarr.add(joo);
        }
        return "{\"table\":{\"key\":\"PhId\",\"newRow\":" + rvarr.toString() + "},\"isChanged\":true}";
    }
}
