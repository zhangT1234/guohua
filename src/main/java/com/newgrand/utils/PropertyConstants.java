package com.newgrand.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/10/9 16:14
 */
public class PropertyConstants {
    private static Properties properties;

    private static void setProperty() {
        if (properties == null) {
            properties = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                properties.load(loader.getResourceAsStream("application.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPropertiesKey(String key) {
        if (properties == null) {
            setProperty();
        }
        return properties.getProperty(key, "default");
    }
}
