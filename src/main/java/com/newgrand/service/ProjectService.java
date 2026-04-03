package com.newgrand.service;

import com.newgrand.domain.dto.OaResult;
import com.newgrand.domain.dto.ProjectSyncRequest;
import com.newgrand.domain.model.I8ReturnModel;
import org.springframework.stereotype.Service;

@Service
public interface ProjectService {
    I8ReturnModel saveData(ProjectSyncRequest data);

    I8ReturnModel syncProj(String pcNo);

    I8ReturnModel syncProjById(Long phid);
}
