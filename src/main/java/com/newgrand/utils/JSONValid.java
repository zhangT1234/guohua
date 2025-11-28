package com.newgrand.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/11/20 15:47
 */
public class JSONValid {

    public static boolean isJSON2(String str) {
        try {
            Object object = JSON.parse(str);
            if (object instanceof JSONObject) {
                return true;
            }
            return object instanceof JSONArray;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isNotJSON2(String str) {
        try {
            Object object = JSON.parse(str);
            if (object instanceof JSONObject) {
                return false;
            }
            return !(object instanceof JSONArray);
        } catch (Exception ex) {
            return true;
        }
    }
}
