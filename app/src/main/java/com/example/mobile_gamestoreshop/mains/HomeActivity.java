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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mobile_gamestoreshop.GameDetailsActivity;
import com.example.mobile_gamestoreshop.MainActivity;
import com.example.mobile_gamestoreshop.R;
import com.example.mobile_gamestoreshop.Adapter.GamesAdapter;
import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.models.Game;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    private RecyclerView popularGamesList, newGamesList, categoriesList;
    private ImageView featuredImage;
    private MaterialButton btnFeaturedAction, btnLoadMore;
    private MaterialCardView featuredCard;

    private GamesAdapter popularAdapter, newAdapter;
    private List<Game> allGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);
        apiService = ApiClient.getApiService(this);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadGames();
    }

    private void initViews() {
        featuredCard = findViewById(R.id.featuredCard);
        featuredImage = findViewById(R.id.featuredImage);
        btnFeaturedAction = findViewById(R.id.btnFeaturedAction);
        btnLoadMore = findViewById(R.id.btnLoadMore);

        popularGamesList = findViewById(R.id.popularGamesList);
        newGamesList = findViewById(R.id.newGamesList);
        categoriesList = findViewById(R.id.categoriesList);

        popularGamesList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        popularAdapter = new GamesAdapter(this, false);
        popularGamesList.setAdapter(popularAdapter);

        newAdapter = new GamesAdapter(this, true);
        newGamesList.setLayoutManager(new GridLayoutManager(this, 2));
        newGamesList.setAdapter(newAdapter);

        btnLoadMore.setOnClickListener(v -> loadMoreGames());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchGames(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        ImageView cartButton = findViewById(R.id.cartButton);
        if (cartButton != null) {
            cartButton.setOnClickListener(v ->
                    Toast.makeText(this, "Корзина", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_library) {
                    Toast.makeText(this, "Библиотека", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(this, "Профиль", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadGames() {
        showLoading(true);

        Call<List<Game>> call = apiService.getAllGames();
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    allGames = response.body();
                    Log.d(TAG, "Loaded games: " + allGames.size());

                    if (!allGames.isEmpty()) {
                        setupFeaturedGame(allGames.get(0));
                        setupPopularGames(allGames);
                        setupNewGames(allGames);
                    } else {
                        Toast.makeText(HomeActivity.this,
                                "Игры не найдены", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this,
                        "Не удалось загрузить игры",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupFeaturedGame(Game game) {
        if (game == null) return;

        if (game.getImageUrl() != null && !game.getImageUrl().isEmpty()) {
            String imageUrl = game.getImageUrl()
                    .replace("localhost", "10.0.2.2")
                    .replace("0.0.0.0", "10.0.2.2");

            Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(featuredImage);
        }

        if (btnFeaturedAction != null) {
            btnFeaturedAction.setOnClickListener(v -> openGameDetails(game));
        }
    }

    private void setupPopularGames(List<Game> games) {
        if (games == null || games.isEmpty()) return;

        int limit = Math.min(10, games.size());
        List<Game> popular = new ArrayList<>(games.subList(0, limit));

        popularAdapter.setGames(popular);
        setupAdapterListeners(popularAdapter);
    }

    private void setupNewGames(List<Game> games) {
        if (games == null || games.isEmpty()) return;

        List<Game> reversed = new ArrayList<>(games);
        Collections.reverse(reversed);
        int limit = Math.min(6, reversed.size());
        List<Game> newGames = new ArrayList<>(reversed.subList(0, limit));

        newAdapter.setGames(newGames);
        setupAdapterListeners(newAdapter);
    }

    private void setupAdapterListeners(GamesAdapter adapter) {
        if (adapter == null) return;

        adapter.setOnGameClickListener(new GamesAdapter.OnGameClickListener() {
            @Override
            public void onGameClick(Game game) {
                if (game != null) openGameDetails(game);
            }

            @Override
            public void onAddToCartClick(Game game) {
                if (game != null) {
                    Toast.makeText(HomeActivity.this,
                            "Добавлено: " + game.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFavoriteClick(Game game) {
                if (game != null) {
                    Toast.makeText(HomeActivity.this,
                            "В избранное: " + game.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchGames(String query) {
        if (allGames == null || query == null) return;

        List<Game> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Game game : allGames) {
            if (game != null && game.getTitle() != null &&
                    game.getTitle().toLowerCase().contains(lowerQuery)) {
                filtered.add(game);
            }
        }

        popularAdapter.setGames(filtered);
        Toast.makeText(this, "Найдено: " + filtered.size(), Toast.LENGTH_SHORT).show();
    }

    private void loadMoreGames() {
        Toast.makeText(this, "Загрузка ещё игр...", Toast.LENGTH_SHORT).show();
    }

    private void openGameDetails(Game game) {
        if (game == null) return;

        Intent intent = new Intent(this, GameDetailsActivity.class);
        intent.putExtra("game", game);
        startActivity(intent);
    }

    private void handleError(Response<?> response) {
        if (response == null) {
            Toast.makeText(this, "Ошибка сервера", Toast.LENGTH_LONG).show();
            return;
        }

        String error;
        int code = response.code();

        if (code == 401) {
            error = "Требуется авторизация";
            logout();
        } else if (code == 404) {
            error = "Ресурс не найден";
        } else if (code >= 500) {
            error = "Ошибка сервера";
        } else {
            error = "Ошибка: " + code;
        }

        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void logout() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }

        ApiClient.resetClient();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        if (btnLoadMore != null) {
            btnLoadMore.setEnabled(!show);
            btnLoadMore.setText(show ? "Загрузка..." : "Загрузить ещё");
        }
    }
}