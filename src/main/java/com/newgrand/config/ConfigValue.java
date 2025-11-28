package com.newgrand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/14 11:53
 */
@Component
public class ConfigValue {

    @Value("${i8.dblink}")
    public String dbConnect;
    @Value("${i8.url}")
    public String i8Url;
}
