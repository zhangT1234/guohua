package com.newgrand.domain.model;

import lombok.Data;

import java.util.List;

/**
 * 订单状态变更接受类型
 *
 * @author zbs
 * @date 2022-11-18 21:21:12
 */
@Data
public class ChangeOrderModel {

    private String bill_no;
    private List<ChangeOrderDetailsModel> details;

}
