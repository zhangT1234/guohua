package com.newgrand.service;

import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;

public interface HtService {
    I8ReturnModel syncHt(String billNo);

    I8ReturnModel syncHtById(Long phid);
}
