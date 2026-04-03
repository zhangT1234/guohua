package com.newgrand.service;

import com.newgrand.domain.dto.CusSyncRequest;
import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.model.I8ReturnModel;
import org.springframework.stereotype.Service;

@Service
public interface CusService {
    I8ReturnModel saveData(CusSyncRequest data);

    I8ReturnModel syncCus(String compNo);

    I8ReturnModel syncCusById(Long phid);
}
