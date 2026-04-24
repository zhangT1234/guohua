package com.newgrand.service;

import com.newgrand.domain.dto.BillRequest;
import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;

public interface BillService {

    I8ReturnModel payBillOaWorkflow(Long phid);

    I8ReturnModel otherPayOaWorkflow(Long phid);

    I8ReturnModel tendPayOaWorkflow(Long phid);

    I8ReturnModel guaranteePayOaWorkflow(Long phid);
}
