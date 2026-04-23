package com.example.mobile_gamestoreshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mobile_gamestoreshop.R;
import com.example.mobile_gamestoreshop.models.Cart;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnRemoveClickListener {
        void onRemoveClick(Cart cartItem);
    }

    private final Context context;
    private List<Cart> cartItems;
    private final OnRemoveClickListener listener;

    public CartAdapter(Context context, List<Cart> cartItems, OnRemoveClickListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateList(List<Cart> newList) {
        this.cartItems = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartItems.get(position);
        if (item.getGame() != null) {
            holder.title.setText(item.getGame().getTitle());
            holder.price.setText(String.format("%.2f ₽ x %d", item.getGame().getPrice(), item.getQuantity()));

            String imageUrl = item.getGame().getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageUrl = imageUrl.replace("localhost", "10.0.2.2").replace("0.0.0.0", "10.0.2.2");
                Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(holder.image);
            }
        }
        holder.removeBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        ImageButton removeBtn;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImage);
            title = itemView.findViewById(R.id.cartItemTitle);
            price = itemView.findViewById(R.id.cartItemPrice);
            removeBtn = itemView.findViewById(R.id.btnRemoveFromCart);
        }
    }
}