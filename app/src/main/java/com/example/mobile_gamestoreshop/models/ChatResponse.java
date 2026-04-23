package com.example.mobile_gamestoreshop.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("reply")
    private String reply;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("error")
    private String error;

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getReply() { return reply; }
    public String getTimestamp() { return timestamp; }
    public String getError() { return error; }
}
