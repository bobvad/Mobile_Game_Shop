package com.example.mobile_gamestoreshop.mains;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_gamestoreshop.ChatAiActivity;
import com.example.mobile_gamestoreshop.MainActivity;
import com.example.mobile_gamestoreshop.R;
import com.example.mobile_gamestoreshop.mains.HomeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigation;
    private TextView userName, userEmail;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        btnLogout = findViewById(R.id.btnLogout);

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Профиль");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }
    private void navigateToAIChat() {
        Intent intent = new Intent(this, ChatAiActivity.class);
        startActivity(intent);
    }
    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    navigateToHome();
                    return true;
                } else if (id == R.id.nav_cart) {
                    navigateToCart();
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                else if (id == R.id.nav_ai_chat) {
                    navigateToAIChat();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadUserData() {
        String userLogin = sharedPreferences.getString("userLogin", "Пользователь");
        String userEmailText = sharedPreferences.getString("userEmail", "email@example.com");

        if (userName != null) {
            userName.setText(userLogin);
        }
        if (userEmail != null) {
            userEmail.setText(userEmailText);
        }
    }

    private void setupListeners() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }


        findViewById(R.id.menuWishlist).setOnClickListener(v ->
                Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show());

    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCart() {
        Intent intent = new Intent(this, com.example.mobile_gamestoreshop.CartActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}