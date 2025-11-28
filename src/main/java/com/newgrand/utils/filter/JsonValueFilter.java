package com.newgrand.utils.filter;

import com.alibaba.fastjson.serializer.ValueFilter;

public class JsonValueFilter implements ValueFilter {
    @Override
    public Object process(Object obj, String s, Object v) {
        if (v == null) {
            return "";
        }
        return v;
    }
}
