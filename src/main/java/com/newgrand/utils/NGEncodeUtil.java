package com.newgrand.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Random;

public class NGEncodeUtil {

    private static final String DESKEY = "d(3D0;Ia";
    private static final char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random randGen = new Random();
    private static long id = 0L;
    private static final String prefix = randomString(5) + "-";

    public static String EncodeStringNG(String aPassword, int aLen) {
        StringBuilder sPassword = new StringBuilder();
        if (aPassword == null) {
            return "";
        } else {
            int iLen = aPassword.length();
            if (iLen == 0) {
                return "";
            } else {
                Random rn = new Random();

                char c;
                int i;
                for(i = 0; i < aLen - iLen - 1; ++i) {
                    int j;
                    if (i == 0) {
                        j = rn.nextInt(34) + 1;
                    } else {
                        j = rn.nextInt(44) + 1;
                    }

                    if (j > 35) {
                        if (iLen == 1) {
                            c = aPassword.charAt(0);
                        } else {
                            c = aPassword.charAt(rn.nextInt(iLen - 1));
                        }
                    } else if (j > 25) {
                        c = (char)(48 + j - 26);
                    } else {
                        c = (char)(97 + j - 1);
                    }

                    sPassword.append(c);
                }

                sPassword.append('a');

                for(i = 0; i < iLen; ++i) {
                    int j = aPassword.charAt(i);
                    if (j >= 'A' && j <= 'Z') {
                        sPassword.append('1');
                    } else {
                        sPassword.append('0');
                    }
                }

                int iOff = sPassword.charAt(0) - 40;
                aPassword = aPassword.toLowerCase();
                StringBuffer sb = new StringBuffer(sPassword.toString());

                for(i = 0; i < iLen; ++i) {
                    c = aPassword.charAt(i);
                    sb.deleteCharAt(iOff - 1);
                    sb.insert(iOff - 1, c);
                    iOff += 3;
                }

                sPassword = new StringBuilder(sb.toString());
                return sPassword.toString();
            }
        }
    }

    public static String decodeStringNG(String aPassword, int aLen) {
        if (aPassword == null) {
            aPassword = "";
        }

        if (aPassword.length() != aLen) {
            return aPassword;
        } else {
            int iLen = aPassword.lastIndexOf(97) + 1;
            String sCaption = aPassword.substring(iLen);
            int iOff = aPassword.charAt(0) - 40;
            StringBuilder sPassword = new StringBuilder();

            String s;
            int i;
            for(i = 0; i < aLen - iLen; ++i) {
                s = aPassword.substring(iOff - 1, iOff);
                sPassword.append(s);
                iOff += 3;
            }

            StringBuffer sPasswordBuffer = new StringBuffer(sPassword.toString());

            for(i = 0; i < aLen - iLen; ++i) {
                s = sCaption.substring(i, i + 1);
                if ("1".equals(s)) {
                    s = Character.toString(sPasswordBuffer.charAt(i));
                    sPasswordBuffer.deleteCharAt(i);
                    sPasswordBuffer.insert(i, s.toUpperCase());
                }
            }

            return sPasswordBuffer.toString();
        }
    }

    public static synchronized String nextID() {
        return prefix + id++;
    }

    public static String randomString(int length) {
        if (length < 1) {
            return null;
        } else {
            char[] randBuffer = new char[length];

            for(int i = 0; i < randBuffer.length; ++i) {
                randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
            }

            return new String(randBuffer);
        }
    }

    public static String desEncryptToHexString(String toEncrypt) {
        if (StrUtil.isBlank(toEncrypt)) {
            return toEncrypt;
        } else {
            DES des = SecureUtil.des(DESKEY.getBytes());
            String encryptHex = des.encryptHex(toEncrypt);
            return encryptHex;
        }
    }

    public static String desDecryptFromHexString(String toDecrypt) {
        if (StrUtil.isBlank(toDecrypt)) {
            return toDecrypt;
        } else {
            DES des = SecureUtil.des(DESKEY.getBytes());
            String decryptStr = des.decryptStr(toDecrypt);
            return decryptStr;
        }
    }

    public static byte[] des3EncodeCbc(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(1, deskey, ips);
        return cipher.doFinal(data);
    }

    public static byte[] des3DecodeCbc(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(2, deskey, ips);
        return cipher.doFinal(data);
    }

}
