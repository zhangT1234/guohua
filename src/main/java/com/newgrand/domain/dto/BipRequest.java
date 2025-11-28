package com.newgrand.domain.dto;

import lombok.Data;

@Data
public class BipRequest<T> {
    private T data;
}
