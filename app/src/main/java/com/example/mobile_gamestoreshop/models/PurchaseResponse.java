package com.example.mobile_gamestoreshop.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PurchaseResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("gameId")
    private int gameId;

    @SerializedName("gameName")
    private String gameName;

    @SerializedName("purchaseDate")
    private Date purchaseDate;

    @SerializedName("keyStatus")
    private String keyStatus;

    @SerializedName("key")
    private String activationKey;

    @SerializedName("message")
    private String message;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getGameId() { return gameId; }
    public String getGameName() { return gameName; }
    public Date getPurchaseDate() { return purchaseDate; }
    public String getKeyStatus() { return keyStatus; }
    public String getActivationKey() { return activationKey; }
    public String getMessage() { return message; }
}