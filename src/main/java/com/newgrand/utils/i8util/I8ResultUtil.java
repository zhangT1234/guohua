package com.newgrand.utils.i8util;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.newgrand.domain.model.I8ReturnModel;
import com.newgrand.utils.JSONValid;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 16:42
 */
public class I8ResultUtil {

    public I8ResultUtil() {
    }

    public static I8ReturnModel success() {
        return success("成功");
    }

    public static I8ReturnModel success(String message) {
        return success(message, "");
    }

    public static <T> I8Result<T> success(String message, T data) {
        return success(message, data, "");
    }

    public static <T> I8Result<T> success(String message, T data, String phId) {
        I8Result<T> dataResult = new I8Result<>();
        dataResult.setCode("0");
        dataResult.setIsOk(true);
        dataResult.setErrorCode("S");
        dataResult.setMessage(message);
        dataResult.setData(data);
        dataResult.setPhid(phId);
        return dataResult;
    }

    public static I8ReturnModel error() {
        return error("失败");
    }

    public static I8ReturnModel error(String message) {
        return error(message, "");
    }

    public static <T> I8Result<T> errorCode(String code, String message) {
        I8Result<T> dataResult = new I8Result<>();
        dataResult.setCode(code);
        dataResult.setIsOk(false);
        dataResult.setErrorCode("E");
        dataResult.setMessage(message);
        dataResult.setData("");
        return dataResult;
    }

    public static <T> I8Result<T> error(String message, T data) {
        I8Result<T> dataResult = new I8Result<>();
        dataResult.setCode("1");
        dataResult.setIsOk(false);
        dataResult.setErrorCode("E");
        dataResult.setMessage(message);
        dataResult.setData(data);
        return dataResult;
    }

    @NotNull
    public static I8ReturnModel getI8Return(String result) {
        if (result.contains("错误摘要")) {
            int begin = result.indexOf("<h3>") + 4;
            int end = result.indexOf("</h3>");
            result = result.substring(begin, end).trim();
        }
        return getI8Return(result, "");
    }

    @NotNull
    public static I8ReturnModel getI8Return(String result, String describe) {
        if (StringUtils.isEmpty(result)) {
            throw new RuntimeException("无返结果，请检查配置参数");
        }
        if (JSONValid.isNotJSON2(result)) {
            throw new RuntimeException(result);
        }
        JSONObject dataRes = JSONObject.parseObject(result);
        if (result.contains("mainbase")) {
            dataRes = dataRes.getJSONObject("mainbase");
        }
        if ("success".equalsIgnoreCase(dataRes.getString("Status")) || "OK".equalsIgnoreCase(dataRes.getString("Status"))) {
            String phId = "";
            if (ObjectUtils.isNotNull(dataRes.get("id"))) {
                phId = dataRes.get("id").toString();
            } else if (ObjectUtils.isNotNull(dataRes.getJSONArray("KeyCodes"))) {
                phId = dataRes.getJSONArray("KeyCodes").get(0).toString();
            }
            return I8ResultUtil.success("接收成功", "", phId);
        }
        if (ObjectUtils.isNull(dataRes.get("Msg"))) {
            throw new RuntimeException(result);
        }
        throw new RuntimeException(describe + dataRes.getString("Msg"));
    }
}
