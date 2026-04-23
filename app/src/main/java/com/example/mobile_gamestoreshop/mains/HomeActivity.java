package com.example.mobile_gamestoreshop.mains;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile_gamestoreshop.CartActivity;
import com.example.mobile_gamestoreshop.ChatAiActivity;
import com.example.mobile_gamestoreshop.GameDetailsActivity;
import com.example.mobile_gamestoreshop.R;
import com.example.mobile_gamestoreshop.Adapter.GamesAdapter;
import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.models.Cart;
import com.example.mobile_gamestoreshop.models.Game;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private int userId = -1;

    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private RecyclerView gamesList;
    private GamesAdapter gamesAdapter;
    private BottomNavigationView bottomNavigation;
    private List<Game> allGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // получаем ID пользователя
        apiService = ApiClient.getApiService(this);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadGames();
    }

    private void initViews() {
        gamesList = findViewById(R.id.gamesList);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        gamesList.setLayoutManager(layoutManager);
        gamesList.setHasFixedSize(true);

        gamesAdapter = new GamesAdapter(this, true);
        gamesList.setAdapter(gamesAdapter);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterGames(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterGames(newText);
                    return false;
                }
            });
        }

        ImageView cartButton = findViewById(R.id.cartButton);
        if (cartButton != null) {
            cartButton.setOnClickListener(v -> navigateToCart());
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_ai_chat) {
                    navigateToAIChat();
                    return true;
                } else if (id == R.id.nav_cart) {
                    navigateToCart();
                    return true;
                } else if (id == R.id.nav_profile) {
                    navigateToProfile();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadGames() {
        Call<List<Game>> call = apiService.getAllGames();
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allGames = response.body();
                    gamesAdapter.setGames(allGames);
                    setupAdapterListeners();
                    Log.d(TAG, "Загружено игр: " + allGames.size());
                    if (allGames.isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Нет игр в базе данных", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) errorBody = response.errorBody().string();
                    } catch (Exception e) {}
                    Log.e(TAG, "Ошибка ответа: код " + response.code() + ", тело: " + errorBody);
                    Toast.makeText(HomeActivity.this, "Ошибка загрузки игр: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Log.e(TAG, "Ошибка сети: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Не удалось подключиться к серверу: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupAdapterListeners() {
        gamesAdapter.setOnGameClickListener(new GamesAdapter.OnGameClickListener() {
            @Override
            public void onGameClick(Game game) {
                if (game != null) openGameDetails(game);
            }

            @Override
            public void onAddToCartClick(Game game) {
                addToCart(game);
            }

            @Override
            public void onFavoriteClick(Game game) {
                if (game != null) {
                    Toast.makeText(HomeActivity.this, "В избранное: " + game.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToCart(Game game) {
        if (game == null) return;
        if (userId == -1) {
            Toast.makeText(this, "Авторизуйтесь для добавления в корзину", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<Cart> call = apiService.addToCart(userId, game.getId(), 1);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Добавлено в корзину: " + game.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterGames(String query) {
        if (allGames == null || query == null) return;

        List<Game> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        if (lowerQuery.isEmpty()) {
            gamesAdapter.setGames(allGames);
            return;
        }

        for (Game game : allGames) {
            if (game != null && game.getTitle() != null &&
                    game.getTitle().toLowerCase().contains(lowerQuery)) {
                filtered.add(game);
            }
        }
        gamesAdapter.setGames(filtered);
    }

    private void openGameDetails(Game game) {
        if (game == null) return;
        Intent intent = new Intent(this, GameDetailsActivity.class);
        intent.putExtra("game", game);
        startActivity(intent);
    }

    private void navigateToCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToAIChat() {
        Intent intent = new Intent(this, ChatAiActivity.class);
        startActivity(intent);
    }
}