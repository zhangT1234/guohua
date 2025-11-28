package com.newgrand.service;

import com.newgrand.domain.model.I8ReturnModel;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/10/8 15:44
 */
public interface ComPushService {

    I8ReturnModel push(String sign, String phid);
}
