package com.newgrand.domain.model;

import lombok.Data;

@Data
public class I8FileBlock {

    private String asr_session_guid;

    private String asr_data;

    private String filename;

    private String fileid;

    private Integer curpart;

    private Integer totalParts;

    private String filemd5;

    private String filesize;

    private Integer asr_part_size;

    private String isAppUpload;

}
