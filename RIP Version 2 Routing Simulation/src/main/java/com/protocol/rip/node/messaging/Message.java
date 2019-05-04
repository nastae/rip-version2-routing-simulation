package com.protocol.rip.node.messaging;

import com.protocol.rip.node.Address;

public class Message {

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