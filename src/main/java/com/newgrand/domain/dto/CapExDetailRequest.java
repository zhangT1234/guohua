package com.newgrand.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CapExDetailRequest {

    //明细单据id
    private Long xzdid;

    //金额
    private BigDecimal appproveAmtFc;

}
