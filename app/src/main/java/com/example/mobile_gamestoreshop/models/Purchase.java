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

    // Сеттеры (добавьте)
    public void setId(int id) { this.id = id; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setKeyStatus(String keyStatus) { this.keyStatus = keyStatus; }
    public void setActivationKey(String activationKey) { this.activationKey = activationKey; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(double price) { this.price = price; }
}