package com.newgrand.utils.i8util;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class PropHelper {
    @Autowired
    private Environment env;

    public String Get(String key) throws IOException {
        String v = env.getProperty(key);
        if (StringUtils.isEmpty(v)) {
            Resource resource = new ClassPathResource("application.yaml");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            v = props.getProperty(key);
        }
        return v;
    }
}
