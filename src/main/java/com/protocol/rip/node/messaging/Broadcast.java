package com.protocol.rip.node.messaging;

public interface Broadcast {

    void send(Message message);

    void receive(Message message);
}
