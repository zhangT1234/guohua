package com.newgrand.utils.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AESUtil {

    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "68d7eec0ba694a68";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    /**
     * aes解密
     * @param encrypt   内容
     */
    public static String aesDecrypt(String encrypt) {
        if (encrypt == null || encrypt.isEmpty()) {
            return "";
        }
        try {
            // 恢复Base64格式
            encrypt = encrypt.replace('-', '+')
                    .replace('_', '/');
            return decrypt(encrypt);
        } catch (Exception e) {
            System.err.println("AES解密失败: " + e.getMessage());
            return "";
        }
    }

    /**
     * aes加密
     */
    public static String aesEncrypt(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        try {
            String encrypted = encrypt(content);
            encrypted = encrypted.replace('+', '-')
                    .replace('/', '_');
            // 替换Base64中的特殊字符
            return encrypted;
        } catch (Exception e) {
            System.err.println("AES加密失败: " + e.getMessage());
            return "";
        }
    }

    private static String encrypt(String strToEncrypt) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AESUtil.KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }

    private static String decrypt(String strToDecrypt) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AESUtil.KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = java.util.Base64.getDecoder().decode(strToDecrypt);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
