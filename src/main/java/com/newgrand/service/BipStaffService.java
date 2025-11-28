package com.newgrand.service;

import com.newgrand.domain.dto.BipRequest;
import com.newgrand.domain.dto.BipResult;
import com.newgrand.domain.dto.BipStaffDTO;
import org.springframework.stereotype.Service;

@Service
public interface BipStaffService {
    BipResult syncStaff(String cNo);

    BipResult testSyncStaff(BipRequest<BipStaffDTO> data);
}
