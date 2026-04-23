package com.example.mobile_gamestoreshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_gamestoreshop.Adapter.CartAdapter;
import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.mains.HomeActivity;
import com.example.mobile_gamestoreshop.mains.ProfileActivity;
import com.example.mobile_gamestoreshop.models.Cart;
import com.example.mobile_gamestoreshop.models.PurchaseResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPriceText, emptyCartText;
    private MaterialButton btnCheckout;
    private CartAdapter adapter;
    private List<Cart> cartItems = new ArrayList<>();
    private ApiService apiService;
    private int userId;
    private BottomNavigationView bottomNavigation;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Необходимо авторизоваться", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        apiService = ApiClient.getApiService(this);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadCart();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        emptyCartText = findViewById(R.id.emptyCartText);
        btnCheckout = findViewById(R.id.btnCheckout);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Корзина");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_cart);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_cart) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_ai_chat) {
                    startActivity(new Intent(this, ChatAiActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void loadCart() {
        Call<List<Cart>> call = apiService.getUserCart(userId);
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body();
                    updateUI();
                } else if (response.code() == 404) {
                    // корзина пуста
                    cartItems.clear();
                    updateUI();
                } else {
                    Toast.makeText(CartActivity.this, "Ошибка загрузки корзины", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
            totalPriceText.setText("0 ₽");
            btnCheckout.setEnabled(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            btnCheckout.setEnabled(true);

            if (adapter == null) {
                adapter = new CartAdapter(this, cartItems, cartItem -> {
                    deleteCartItem(cartItem.getId());
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateList(cartItems);
            }
            double total = 0;
            for (Cart item : cartItems) {
                if (item.getGame() != null)
                    total += item.getGame().getPrice() * item.getQuantity();
            }
            totalPriceText.setText(String.format("%.2f ₽", total));
        }
    }

    private void deleteCartItem(int cartId) {
        Call<ResponseBody> call = apiService.removeFromCart(cartId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Товар удалён", Toast.LENGTH_SHORT).show();
                    loadCart(); // перезагрузить корзину
                } else {
                    Toast.makeText(CartActivity.this, "Не удалось удалить", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Оформление заказа");
        builder.setMessage("Пожалуйста, подождите...");
        builder.setCancelable(false);
        progressDialog = builder.show();

        // Покупаем каждый товар через API Purchases
        buyNextItem(0, new ArrayList<>());
    }

    private void buyNextItem(int index, List<PurchaseResponse> results) {
        if (index >= cartItems.size()) {
            progressDialog.dismiss();
            StringBuilder message = new StringBuilder("Покупка завершена!\n\n");
            for (PurchaseResponse res : results) {
                message.append(res.getGameName()).append(":\nКлюч: ").append(res.getActivationKey()).append("\n\n");
            }
            new AlertDialog.Builder(this)
                    .setTitle("Успешно")
                    .setMessage(message.toString())
                    .setPositiveButton("OK", (d, w) -> {
                        // очистить корзину на сервере
                        clearCartOnServer();
                    })
                    .show();
            return;
        }

        Cart item = cartItems.get(index);
        int gameId = item.getGameId();
        Call<PurchaseResponse> call = apiService.buyGame(userId, gameId);
        call.enqueue(new Callback<PurchaseResponse>() {
            @Override
            public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    results.add(response.body());
                    buyNextItem(index + 1, results);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(CartActivity.this, "Ошибка при покупке игры", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CartActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearCartOnServer() {
        Call<ResponseBody> call = apiService.clearCart(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadCart(); // перезагружаем корзину (она станет пустой)
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // игнорируем
            }
        });
    }
}