package org.example.netty.Entity;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private int sequenceId;
    public  int getSequenceId() {
        return this.sequenceId;
    }

    public static final int PingMessageType = 1;
    public static final int ChatMessageType = 2;
    public abstract int getMessageType();
    private int messageType;
    private String content;

}
