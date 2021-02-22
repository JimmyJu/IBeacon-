package com.example.ibeacondemo;

public class MessageWrap {
    public final boolean bool;

    public static MessageWrap getInstance(boolean bool) {
        return new MessageWrap(bool);
    }

    private MessageWrap(boolean bool) {
        this.bool = bool;
    }
}
