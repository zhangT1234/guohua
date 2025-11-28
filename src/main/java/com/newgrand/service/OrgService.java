package com.newgrand.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.dto.OrgSyncRequest;
import com.newgrand.domain.model.*;
import com.newgrand.mapper.UIPCommonMapper;
import com.newgrand.utils.i8util.EntityConverter;
import com.newgrand.utils.i8util.I8Request;
import com.newgrand.utils.i8util.StringHelper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: zhanglixin
 * @Data: 2022/9/12 14:40
 * @Description: 组织数据保存
 */
@Service
public interface OrgService {
    I8ReturnModel saveOrgOrDept(OrgSyncRequest data);
}