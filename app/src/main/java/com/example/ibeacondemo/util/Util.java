package com.example.ibeacondemo.util;

import android.text.TextUtils;

import com.tamsiree.rxkit.RxConstTool;

import org.apache.commons.lang3.StringUtils;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Util {
    /*
     * 16位转8
     * */
    public static byte[] merge2BytesTo1Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        char s, e;
        for (int i = 0; i < str.length(); i += 2) {
            s = str.charAt(i);//第I个索引字符
            e = str.charAt(i + 1);//第I+1个索引字符
            if (s <= '9') {
                if (e <= '9') {
                    bytes[i / 2] = (byte) (((s - 0x30) << 4) + (e - 0x30));
                } else if ((e <= 'z' && e >= 'a')) {
                    bytes[i / 2] = (byte) (((s - 0x30) << 4) + (e - 0x57));//左移4位

                }
            } else if (s > '9') {
                if (e <= '9') {
                    bytes[i / 2] = (byte) (((s - 0x57) << 4) + (e - 0x30));
                } else if ((s <= 'z' && e >= 'a')) {
                    bytes[i / 2] = (byte) (((s - 0x57) << 4) + (e - 0x57));
                }
            }
        }
        return bytes;
    }

    /*
     * DES加密
     * */

    public static byte[] encode(byte[] data, byte[] key) throws Exception {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] bytes = cipher.doFinal(data);
            return bytes;
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
        MessageDigest md5 = null;
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

    public static String bytesToHexString(byte[] src) {
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

    /**
     * 判断字符串是否为json格式
     */
    public static boolean getJSONType(String str) {
        boolean result = false;
        if (StringUtils.isNotBlank(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 判断该邮件地址是否合法
     *
     * @param address 邮件地址，可以多个，逗号隔开
     * @return
     */
    public static boolean isEmailAddress(String address) {
        // 是否合法
        boolean flag = false;
        if (StringUtils.isEmpty(address)) {
            return false;
        }
        try {
            String[] addressArr = address.split(",");
//            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(RxConstTool.REGEX_EMAIL);
            Matcher matcher = null;
            for (String str : addressArr) {
                matcher = regex.matcher(str);
                flag = matcher.matches();
                if (!flag) {
                    return false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }



    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
     *
     * @param content
     *            传入的字符串
     * @param frontNum
     *            保留前面字符的位数
     * @param endNum
     *            保留后面字符的位数
     * @return 带星号的字符串
     */

    public static String getStarString(String content, int frontNum, int endNum) {

        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, frontNum) + starStr
                + content.substring(content.length() - endNum, content.length());

    }
}
