package com.example.mobile_gamestoreshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile_gamestoreshop.models.ChatMessage;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private final List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    public void removeMessageById(long id) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId() == id) {
                messages.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.messageText.setText(msg.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                holder.messageContainer.getLayoutParams();

        if (msg.getSender() == ChatMessage.Sender.USER) {
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_user);
            holder.messageText.setTextColor(0xFFFFFFFF);
            params.gravity = android.view.Gravity.END;
        } else {
            holder.messageContainer.setBackgroundResource(R.drawable.bg_message_ai);
            holder.messageText.setTextColor(0xFF000000);
            params.gravity = android.view.Gravity.START;
        }
        holder.messageContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        LinearLayout messageContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
            messageContainer = itemView.findViewById(R.id.containerMessage);
        }
    }
}