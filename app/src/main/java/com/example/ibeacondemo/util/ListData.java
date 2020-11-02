package com.example.ibeacondemo.util;

public class ListData {
    private String Key;
    private String DES;
    private String MD5End;
    private String XOR;
    private String UUid;
    private long Major;
    private long Minor;

    public ListData(String key, String DES, String MD5End, String XOR, String UUid, long major, long minor) {
        Key = key;
        this.DES = DES;
        this.MD5End = MD5End;
        this.XOR = XOR;
        this.UUid = UUid;
        Major = major;
        Minor = minor;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDES() {
        return DES;
    }

    public void setDES(String DES) {
        this.DES = DES;
    }

    public String getMD5End() {
        return MD5End;
    }

    public void setMD5End(String MD5End) {
        this.MD5End = MD5End;
    }

    public String getXOR() {
        return XOR;
    }

    public void setXOR(String XOR) {
        this.XOR = XOR;
    }

    public String getUUid() {
        return UUid;
    }

    public void setUUid(String UUid) {
        this.UUid = UUid;
    }

    public long getMajor() {
        return Major;
    }

    public void setMajor(long major) {
        Major = major;
    }

    public long getMinor() {
        return Minor;
    }

    public void setMinor(long minor) {
        Minor = minor;
    }
}
