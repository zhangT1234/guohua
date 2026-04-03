package com.newgrand.service;

import com.newgrand.domain.dto.ReceiptNoticeRequest;
import com.newgrand.domain.model.I8ReturnModel;

public interface ReceiptNoticeService {

    I8ReturnModel saveReceiptNotice(ReceiptNoticeRequest data);

}
