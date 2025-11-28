package com.newgrand.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 17:38
 */
public interface AttachmentService {

    void downLoad(HttpServletResponse httpServletResponse, String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException;

}
