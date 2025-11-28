package com.newgrand.service;

import com.newgrand.domain.dto.BipPayableDTO;
import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import org.springframework.stereotype.Service;

@Service
public interface BipPayableService {
    BipResult testSyncPayable(BipRequest<BipPayableDTO> data);

    BipResult labor(String billNo);

    BipResult other(String billNo);

    BipResult purchase(String billNo);

    BipResult check(String billNo);
}
