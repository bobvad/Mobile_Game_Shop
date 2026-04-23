package com.example.mobile_gamestoreshop;

public class ChatMessage {
    public enum Sender { USER, AI }

    private final long id;
    private final String text;
    private final com.example.mobile_gamestoreshop.models.ChatMessage.Sender sender;
    private final long timestamp;

    public ChatMessage(String text, com.example.mobile_gamestoreshop.models.ChatMessage.Sender sender) {
        this.id = System.currentTimeMillis();
        this.text = text;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public String getText() { return text; }
    public com.example.mobile_gamestoreshop.models.ChatMessage.Sender getSender() { return sender; }
    public long getTimestamp() { return timestamp; }
}
