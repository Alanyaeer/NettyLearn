package org.example.netty.Entity.message;

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

    @Override
    public String toString() {
        return "ChatMessage{" +
                "Content='" + Content + '\'' +
                '}';
    }
}
