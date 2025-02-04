package org.example.netty.Entity;

public class ChatMessage extends Message{
    @Override
    public int getMessageType() {
        return ChatMessageType;
    }
    private String Content;

    public void setContent(String content) {
        Content = content;
    }

    public String getContent() {
        return Content;
    }
}
