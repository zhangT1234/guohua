package com.newgrand.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class SyncResultDto {

    private Integer success;

    private Integer fail;

    private List<String> msg;

}
