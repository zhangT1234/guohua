package com.newgrand.service;

import com.newgrand.domain.dto.BipReceivableDTO;
import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import org.springframework.stereotype.Service;

@Service
public interface BipReceivableService {
    BipResult testSyncReceivable(BipRequest<BipReceivableDTO> data);

    BipResult factCz(String billNo);

    BipResult otherCz(String billNo);

    BipResult otherSettlement(String billNo);

    BipResult contractSettlementJl(String billNo);

    BipResult otherSettlementJl(String billNo);
}
