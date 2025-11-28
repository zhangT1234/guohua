package com.newgrand.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BipResult<T> {
    private String code;

    private String message;

    private T data;
}
