// UserStats.java
package com.example.mobile_gamestoreshop.models;

public class UserStats {
    private int gamesCount;    // Количество игр у пользователя
    private int ordersCount;   // Количество заказов пользователя

    // Конструкторы
    public UserStats() {}

    public UserStats(int gamesCount, int ordersCount) {
        this.gamesCount = gamesCount;
        this.ordersCount = ordersCount;
    }

    // Геттеры и сеттеры
    public int getGamesCount() {
        return gamesCount;
    }

    public void setGamesCount(int gamesCount) {
        this.gamesCount = gamesCount;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }
}