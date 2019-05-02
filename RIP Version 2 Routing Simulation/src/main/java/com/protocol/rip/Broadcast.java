package com.protocol.rip;

public interface Broadcast {

    void send(Message message);

    void receive(Message message);
}
