package com.newgrand.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/10/9 16:14
 */
@Component
public class YamlConstants {

    public static String getPropertiesKey(String key) {
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        yamlMapFactoryBean.setResources(new ClassPathResource("application.yaml"));
        Properties properties = yamlMapFactoryBean.getObject();
        String active = properties.getProperty("spring.profiles.active");
        yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        yamlMapFactoryBean.setResources(new ClassPathResource("application-" + active + ".yaml"));
        properties = yamlMapFactoryBean.getObject();
        //获取yml里的参数
        String param = properties.getProperty(key);
        if (StringUtils.isEmpty(param)) {
            return "";
        }
        return param;
    }
}
