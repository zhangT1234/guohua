package com.newgrand.utils.i8util;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.stereotype.Component;

@Component
public class BoPoMoFoUtil {

    public String getChineseInitials(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isChinese(c)) {
                // 获取汉字的所有拼音（可能包含多音字）
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    // 取第一个拼音的首字母并大写
                    result.append(pinyinArray[0].charAt(0));
                }
            } else {
                // 非中文字符直接保留
                result.append(c);
            }
        }
        return result.toString().toUpperCase();
    }

    private boolean isChinese(char c) {
        return (c >= 0x4E00 && c <= 0x9FA5);
    }
}
