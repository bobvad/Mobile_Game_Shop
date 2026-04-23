package com.example.mobile_gamestoreshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_gamestoreshop.api.ApiClient;
import com.example.mobile_gamestoreshop.api.ApiService;
import com.example.mobile_gamestoreshop.models.ChatMessage;
import com.example.mobile_gamestoreshop.models.ChatResponse;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private ApiService apiService;
    private String sessionId;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ai);

        // Получаем userId из SharedPreferences (если пользователь авторизован)
        SharedPreferences prefs = getSharedPreferences("GameShopPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) userId = null;

        // Генерируем уникальный ID сессии для гостей
        sessionId = UUID.randomUUID().toString();

        apiService = ApiClient.getApiService(this);

        initViews();
        setupToolbar();
        setupChat();

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ИИ-Ассистент");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupChat() {
        adapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ChatMessage welcomeMsg = new ChatMessage("Привет! Я ваш игровой ассистент. Задавайте любые вопросы об играх, акциях или технической поддержке.", ChatMessage.Sender.AI);
        messageList.add(welcomeMsg);
        adapter.notifyItemInserted(0);
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) return;

        ChatMessage userMessage = new ChatMessage(messageText, ChatMessage.Sender.USER);
        messageList.add(userMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
        editTextMessage.setText("");

        Call<ChatResponse> call = apiService.sendChatMessage(messageText, userId, sessionId);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String reply = response.body().getReply();
                    ChatMessage aiMessage = new ChatMessage(reply, ChatMessage.Sender.AI);
                    messageList.add(aiMessage);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                } else {
                    String errorMsg = response.body() != null ? response.body().getError() : "Ошибка сервера";
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                showError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

        ChatMessage errorMessage = new ChatMessage("Извините, произошла ошибка. Попробуйте позже.", ChatMessage.Sender.AI);
        messageList.add(errorMessage);
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private final List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.textMessage.setText(msg.getText());

            if (msg.getSender() == ChatMessage.Sender.USER) {
                holder.containerMessage.setBackgroundResource(R.drawable.bg_message_user);
                holder.textMessage.setTextColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = android.view.Gravity.END;
                holder.containerMessage.setLayoutParams(params);
            } else {
                holder.containerMessage.setBackgroundResource(R.drawable.bg_message_ai);
                holder.textMessage.setTextColor(0xFF000000);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = android.view.Gravity.START;
                holder.containerMessage.setLayoutParams(params);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout containerMessage;
            TextView textMessage;

            ViewHolder(View itemView) {
                super(itemView);
                containerMessage = itemView.findViewById(R.id.containerMessage);
                textMessage = itemView.findViewById(R.id.textMessage);
            }
        }
    }
}