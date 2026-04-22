package com.example.mobile_gamestoreshop;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mobile_gamestoreshop.models.Game;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class GameDetailsActivity extends AppCompatActivity {

    private ImageView gameCover;
    private TextView gameTitle, gameDescription, gamePrice, gameDeveloper,
            gamePublisher, gameReleaseDate, gamePlatform, gameAgeRating;
    private MaterialButton btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        initViews();
        setupToolbar();
        loadGameData();
    }

    private void initViews() {
        gameCover = findViewById(R.id.gameCover);
        gameTitle = findViewById(R.id.gameTitle);
        gameDescription = findViewById(R.id.gameDescription);
        gamePrice = findViewById(R.id.gamePrice);
        gameDeveloper = findViewById(R.id.gameDeveloper);
        gamePublisher = findViewById(R.id.gamePublisher);
        gameReleaseDate = findViewById(R.id.gameReleaseDate);
        gamePlatform = findViewById(R.id.gamePlatform);
        gameAgeRating = findViewById(R.id.gameAgeRating);
        btnBuy = findViewById(R.id.btnBuy);

        btnBuy.setOnClickListener(v -> {
            Toast.makeText(this, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadGameData() {
        Game game = (Game) getIntent().getSerializableExtra("game");

        if (game == null) {
            Toast.makeText(this, "Ошибка загрузки игры", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Загрузка данных
        gameTitle.setText(game.getTitle());
        gamePrice.setText(String.format("%.2f ₽", game.getPrice()));
        gameDescription.setText(game.getDescription() != null ? game.getDescription() : "Описание отсутствует");
        gameDeveloper.setText("Разработчик: " + (game.getDeveloper() != null ? game.getDeveloper() : "Не указан"));
        gamePublisher.setText("Издатель: " + (game.getPublisher() != null ? game.getPublisher() : "Не указан"));
        gamePlatform.setText("Платформа: " + (game.getPlatform() != null ? game.getPlatform() : "PC"));
        gameAgeRating.setText("Возраст: " + (game.getAgeRating() != null ? game.getAgeRating() : "Не указан"));
        gameReleaseDate.setText("Дата выхода: " + (game.getReleaseDate() != null ? game.getReleaseDate() : "Не указана"));

        // Загрузка обложки
        if (game.getImageUrl() != null && !game.getImageUrl().isEmpty()) {
            String imageUrl = game.getImageUrl()
                    .replace("localhost", "10.0.2.2")
                    .replace("0.0.0.0", "10.0.2.2");
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(gameCover);
        }
    }
}