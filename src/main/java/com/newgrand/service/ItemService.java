package com.newgrand.service;

import com.newgrand.domain.dto.ItemDataRequest;
import com.newgrand.domain.dto.ItemResRequest;
import com.newgrand.domain.model.I8ReturnModel;

public interface ItemService {

    I8ReturnModel saveItemResData(ItemResRequest data);

    I8ReturnModel saveItemData(ItemDataRequest data);

}
