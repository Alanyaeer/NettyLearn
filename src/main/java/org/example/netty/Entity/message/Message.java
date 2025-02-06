package org.example.netty.Entity.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();
    static {
        messageClasses.put(PingMessageType, PingMessage.class);
        messageClasses.put(ChatMessageType, ChatMessage.class);
    }
    public static Class<?> getMessageClass(Byte messageType){
        return messageClasses.get(messageType.intValue());
    }
}
