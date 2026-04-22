// models/Purchase.java
package com.example.mobile_gamestoreshop.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Purchase {
    @SerializedName("id")
    private int id;

    @SerializedName("gameId")
    private int gameId;

    @SerializedName("gameName")
    private String gameName;

    @SerializedName("purchaseDate")
    private Date purchaseDate;

    @SerializedName("keyStatus")
    private String keyStatus;

    @SerializedName("activationKey")
    private String activationKey;

    @SerializedName("platform")
    private String platform;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("price")
    private double price;

    // Геттеры
    public int getId() { return id; }
    public int getGameId() { return gameId; }
    public String getGameName() { return gameName; }
    public Date getPurchaseDate() { return purchaseDate; }
    public String getKeyStatus() { return keyStatus; }
    public String getActivationKey() { return activationKey; }
    public String getPlatform() { return platform; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
}