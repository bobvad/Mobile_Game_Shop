// models/Game.java
package com.example.mobile_gamestoreshop.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Game implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("releaseDate")
    private Date releaseDate;

    @SerializedName("developer")
    private String developer;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("ageRating")
    private String ageRating;

    @SerializedName("platform")
    private String platform;

    @SerializedName("imageUrl")
    private String imageUrl;

    public Game() {}

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public Date getReleaseDate() { return releaseDate; }
    public String getDeveloper() { return developer; }
    public String getPublisher() { return publisher; }
    public String getAgeRating() { return ageRating; }
    public String getPlatform() { return platform; }
    public String getImageUrl() { return imageUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}