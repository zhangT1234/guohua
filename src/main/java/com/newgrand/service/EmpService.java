package com.newgrand.service;

import com.newgrand.domain.dto.EmpModel;
import com.newgrand.domain.model.*;
import org.springframework.stereotype.Service;

@Service
public interface EmpService {
    I8ReturnModel saveEmp(EmpModel data);
}
