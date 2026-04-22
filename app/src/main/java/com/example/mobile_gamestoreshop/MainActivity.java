// MainActivity.java
package com.example.mobile_gamestoreshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.mains.HomeActivity;
import com.example.mobile_gamestoreshop.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText loginEditText, passwordEditText;
    private TextInputLayout loginLayout, passwordLayout;
    private Button loginButton;
    private TextView forgotPasswordText, registerText;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        apiService = ApiClient.getApiService(this);

        initViews();

        setupListeners();
    }

    private void initViews() {
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginLayout = findViewById(R.id.loginLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        registerText = findViewById(R.id.registerText);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        ((androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.main)).addView(progressBar);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin());

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(this, "Функция восстановления пароля будет доступна позже", Toast.LENGTH_LONG).show();
        });
    }

    private void performLogin() {
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (login.isEmpty()) {
            loginLayout.setError("Введите логин");
            return;
        } else {
            loginLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Введите пароль");
            return;
        } else {
            passwordLayout.setError(null);
        }

        setLoading(true);

        Call<User> call = apiService.signIn(login, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("userId", user.getId());
                    editor.putString("userLogin", user.getLogin());
                    editor.putString("userEmail", user.getEmail());
                    editor.putString("userRole", user.getRole());
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Добро пожаловать, " + user.getLogin() + "!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Ошибка авторизации";
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Ошибка авторизации. Проверьте логин и пароль.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                Toast.makeText(MainActivity.this, "Ошибка подключения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        loginButton.setText(isLoading ? "" : "Войти");
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}