package com.newgrand.utils.i8util;

import com.newgrand.domain.model.I8FileBlock;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class I8FileUtil {

    public static List<I8FileBlock> getFileBlock(String asr_session_guid, String filename, byte[] data) {
        List<I8FileBlock> blocks = new ArrayList<>();

        Integer fileLen = data.length;
        String filesize = String.valueOf(fileLen);
        String filemd5 = DigestUtils.md5DigestAsHex(data);

        //剩余字节长度
        Integer leftlen = fileLen;
        //字节数组开始下标
        Integer startpos = 0;
        Integer buffersendsize = 1024 * 1024;
        Integer curpart = 0;
        //leftlen小于buffersize时 以下方法计算出来的totalParts为0
        //leftlen大于buffersize时 以下方法计算出来的totalParts为实际分块减1
        //所以leftlen不等于buffersize时，totalParts需要加1处理
        Integer totalParts = leftlen / buffersendsize;
        if (leftlen != buffersendsize) {
            totalParts += 1;
        }

        //分块上传
        while (leftlen > 0) {
            byte[] buffersend;
            if (leftlen > buffersendsize) {
                buffersendsize = 1024 * 1024;
            } else {
                buffersendsize = leftlen;
            }
            //创建缓存数组
            buffersend = new byte[buffersendsize];
            System.arraycopy(data, startpos, buffersend, 0, buffersendsize);
            startpos += buffersendsize;
            leftlen -= buffersendsize;
//            String Part = String.valueOf(curpart);
            String asr_data = Base64.getEncoder().encodeToString(buffersend);

            I8FileBlock i8FileBlock = new I8FileBlock();
            i8FileBlock.setAsr_session_guid(asr_session_guid);
            i8FileBlock.setAsr_data(asr_data);
            i8FileBlock.setFileid(asr_session_guid);
            i8FileBlock.setFilename(filename);
            i8FileBlock.setCurpart(curpart);
            i8FileBlock.setTotalParts(totalParts);
            i8FileBlock.setFilemd5(filemd5);
            i8FileBlock.setFilesize(filesize);
            i8FileBlock.setAsr_part_size(buffersendsize);
            i8FileBlock.setIsAppUpload("0");
            blocks.add(i8FileBlock);

            if (curpart + 1 == totalParts) {
                break;
            } else {
                curpart += 1;
            }
        }
        return blocks;
    }

    public static String md5(byte[] str) {
        String res = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str);
            byte b[] = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            res = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return res;
    }
}
