package com.protocol.rip;

import java.io.Serializable;

public class Message implements Serializable {

    private Address destAddr;
    private String text;

    public Message(Address destAddr, String text) {
        this.destAddr = destAddr;
        this.text = text;
    }

    public Address getDestAddr() {
        return destAddr;
    }

    public String getText() {
        return text;
    }
}