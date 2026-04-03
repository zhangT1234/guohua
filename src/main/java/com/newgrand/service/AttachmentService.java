package com.newgrand.service;

import com.newgrand.domain.model.I8FileModel;
import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ZhaoFengjie
 * @version 1.0
 * @date 2022/8/11 17:38
 */
public interface AttachmentService {

    void downLoad(HttpServletResponse httpServletResponse, String asr_code, String asr_table, String asr_attach_table, String asr_filename) throws IOException;

    boolean upLoadFile(I8FileModel i8FileModel);

    boolean save(String asr_guid, String asr_code, String asr_mode) throws DocumentException;

    boolean blockUpload(String asr_session_guid, String asr_data, String fileid, String filename, Integer curpart, Integer totalparts, String filemd5, String filesize, Integer asr_part_size, String isAppUpload) throws DocumentException;

    boolean postFileItem(I8FileModel data);

    boolean initEx(String asr_session_guid, String asr_attach_table, String asr_code, String asr_table, String asr_fill, String asr_fillname) throws DocumentException;


}
