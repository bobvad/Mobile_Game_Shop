// RegisterActivity.java
package com.example.mobile_gamestoreshop;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etLogin, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout loginLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getApiService(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etLogin = findViewById(R.id.etLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        loginLayout = findViewById(R.id.loginLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.tvLoginLink).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegistration());
    }

    private void performRegistration() {
        String login = etLogin.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean isValid = true;

        if (login.isEmpty()) {
            loginLayout.setError("Введите логин");
            isValid = false;
        } else if (login.length() < 3) {
            loginLayout.setError("Логин должен содержать минимум 3 символа");
            isValid = false;
        } else {
            loginLayout.setError(null);
        }

        if (email.isEmpty()) {
            emailLayout.setError("Введите email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Введите корректный email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Пароль должен содержать минимум 6 символов");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Пароли не совпадают");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        if (!isValid) return;

        setLoading(true);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Call<User> call = apiService.register(login, email, password, currentDate);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Регистрация успешна! Теперь вы можете войти.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Ошибка регистрации";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Ошибка регистрации. Попробуйте другой логин.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Ошибка подключения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        btnRegister.setEnabled(!isLoading);
        btnRegister.setText(isLoading ? "" : "Зарегистрироваться");
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}