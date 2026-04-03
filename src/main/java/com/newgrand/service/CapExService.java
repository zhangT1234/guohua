package com.newgrand.service;

import com.newgrand.domain.dto.CapExRequest;
import com.newgrand.domain.model.I8ReturnModel;

public interface CapExService {

    I8ReturnModel updateCapEx(CapExRequest capExRequest);

}
