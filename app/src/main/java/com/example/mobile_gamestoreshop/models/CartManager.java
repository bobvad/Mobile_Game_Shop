package com.example.mobile_gamestoreshop.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobile_gamestoreshop.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "cart_prefs";
    private static final String KEY_CART = "cart_items";
    private static CartManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private List<CartItem> cartItems;

    private CartManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        String json = prefs.getString(KEY_CART, "");
        if (json.isEmpty()) {
            cartItems = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(json, type);
            if (cartItems == null) cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String json = gson.toJson(cartItems);
        prefs.edit().putString(KEY_CART, json).apply();
    }

    public void addToCart(CartItem item) {
        for (CartItem existing : cartItems) {
            if (existing.getGameId() == item.getGameId()) {
                return; // уже есть
            }
        }
        cartItems.add(item);
        saveCart();
    }

    public void removeFromCart(int gameId) {
        cartItems.removeIf(item -> item.getGameId() == gameId);
        saveCart();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice();
        }
        return total;
    }

    public int getItemCount() {
        return cartItems.size();
    }
}
