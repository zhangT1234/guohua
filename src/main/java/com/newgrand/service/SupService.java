package com.newgrand.service;

import com.newgrand.domain.dto.SupSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import org.springframework.stereotype.Service;

@Service
public interface SupService {
    I8ReturnModel saveData(SupSyncRequest data);
}
