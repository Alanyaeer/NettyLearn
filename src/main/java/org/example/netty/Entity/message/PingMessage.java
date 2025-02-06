package org.example.netty.Entity.message;

public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PingMessageType;
    }

    @Override
    public String toString() {
        return "PingMessage{}";
    }
}
