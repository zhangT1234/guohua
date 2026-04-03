package com.newgrand.service;

import com.newgrand.domain.dto.AccRequest;
import com.newgrand.domain.model.I8ReturnModel;

public interface AccountService {

    I8ReturnModel save(AccRequest data);

}
