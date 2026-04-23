package com.example.mobile_gamestoreshop.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int gameId;
    private String title;
    private double price;
    private String imageUrl;

    public CartItem(int gameId, String title, double price, String imageUrl) {
        this.gameId = gameId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getGameId() { return gameId; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}