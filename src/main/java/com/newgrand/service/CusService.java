package com.newgrand.service;

import com.newgrand.domain.dto.CusSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import org.springframework.stereotype.Service;

@Service
public interface CusService {
    I8ReturnModel saveData(CusSyncRequest data);
}
