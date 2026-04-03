package com.newgrand.service;

import com.newgrand.domain.dto.EmpModel;
import com.newgrand.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmpService {
    I8ReturnModel saveEmp(EmpModel data);

    I8ReturnModel saveEmpList(List<EmpModel> list);
}
