package com.example.ibeacondemo.util;

import android.text.TextUtils;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Util {
    /*
     * 16位转8
     * */
    public static byte[] merge2BytesTo1Byte(String str){
        byte[] bytes =new byte[str.length()/2];
        char s,e;
        for(int i=0;i<str.length();i+=2){
            s=str.charAt(i);//第I个索引字符
            e=str.charAt(i+1);//第I+1个索引字符
            if( s<='9')
            {
                if(e <='9')
                {
                    bytes[i/2]=(byte)(((s-0x30)<<4)+(e-0x30));
                }
                else if((e<='z' && e>='a'))
                {
                    bytes[i/2]=(byte)(((s-0x30)<<4)+(e-0x57));//左移4位

                }
            }
            else if(s>'9')
            {
                if(e<='9')
                {
                    bytes[i/2]=(byte)(((s-0x57)<<4)+(e-0x30));
                }
                else if((s<='z' && e>='a'))
                {
                    bytes[i/2]=(byte)(((s-0x57)<<4)+(e-0x57));
                }
            }
        }
        return bytes;
    }

    /*
    * DES加密
    * */

    public static byte[] encode(byte[] data,byte[] key) throws Exception {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] bytes = cipher.doFinal(data);
            return  bytes;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /*
     * 拼接数组
     * */
    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /*
     * byte转String
     * */

    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /*
    * MD5计算
    * */

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null ;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
    * 异或
    * */

    public static String XorEncryptAndBaseNew(String str, String s) {
        byte[] b1 = hexStringToBytes(str);
        byte[] b2 = hexStringToBytes(s);
        byte[] out = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            out[i] = (byte) (b1[i] ^ b2[i % b2.length]);
        }
        return bytesToHexString(out);
    }

    /*
    * byte转String
    * */

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /*
    * String转byte
    * */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


}
