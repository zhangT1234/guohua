package com.newgrand.domain.model;

import lombok.Data;

/**
 * @Author: zhanglixin
 * @Data: 2025/1/9 14:06
 * @Description: TODO
 */
@Data
public class I8FileModel {

    private String asr_session_guid;

    private String asr_attach_table;

    private String asr_table;

    private String asr_code;

    private String asr_fill;

    private String asr_fillname;

    private String asr_dbconn;

    private String asr_address;

    private byte[] asr_data;

    private String asr_url;

    private String asr_data_base64;
}

