package com.newgrand.config;

import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @创建人：ZhaoFengjie
 * @修改人：ZhaoFengjie
 * @创建时间：15:12 2022/9/15
 * @修改时间:：15:12 2022/9/15
 */
@Component
@ConfigurationPropertiesBinding
public class ObjectWrapperFactoryConverter implements Converter<String, ObjectWrapperFactory> {
    @Override
    public ObjectWrapperFactory convert(String source) {
        try {
            return (ObjectWrapperFactory) Class.forName(source).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
