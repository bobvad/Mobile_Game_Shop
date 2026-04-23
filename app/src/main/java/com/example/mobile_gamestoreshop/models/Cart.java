package com.example.mobile_gamestoreshop.models;
import com.google.gson.annotations.SerializedName;

public class Cart {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("gameId")
    private int gameId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("game")
    private Game game;  // вложенный объект игры (приходит из Include)

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getGameId() { return gameId; }
    public int getQuantity() { return quantity; }
    public Game getGame() { return game; }
}