package com.newgrand.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OaResult<T> {

    private String code;

    private String msg;

    private T data;
}