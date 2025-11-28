package com.newgrand.config;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：15:10 2022/9/15
 * @修改时间:：15:10 2022/9/15
 */
public class MapKeyLowerWrapper extends MapWrapper {

    public MapKeyLowerWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name == null ? "" : name.toLowerCase();
    }
}
