package org.example.netty.Entity;

public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PingMessageType;
    }

}
