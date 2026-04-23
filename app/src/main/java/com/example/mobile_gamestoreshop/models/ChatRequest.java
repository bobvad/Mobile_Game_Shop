package com.example.mobile_gamestoreshop.models;

public class ChatRequest {
    private String message;
    private Integer userId;
    private String sessionId;

    public ChatRequest(String message, Integer userId, String sessionId) {
        this.message = message;
        this.userId = userId;
        this.sessionId = sessionId;
    }

    // Геттеры (нужны для Gson)
    public String getMessage() { return message; }
    public Integer getUserId() { return userId; }
    public String getSessionId() { return sessionId; }
}
