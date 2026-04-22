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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mobile_gamestoreshop.R;
import com.example.mobile_gamestoreshop.models.Game;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameViewHolder> {
    public interface OnGameClickListener {
        void onGameClick(Game game);
        void onAddToCartClick(Game game);
        void onFavoriteClick(Game game);
    }

    private final Context context;
    private List<Game> games = new ArrayList<>();
    private OnGameClickListener listener;
    private final boolean showFullInfo;

    public GamesAdapter(Context context, boolean showFullInfo) {
        this.context = context;
        this.showFullInfo = showFullInfo;
    }

    public void setGames(List<Game> games) {
        this.games = games != null ? games : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnGameClickListener(OnGameClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);

        if (game.getImageUrl() != null && !game.getImageUrl().isEmpty()) {
            String imageUrl = convertToLocalhost(game.getImageUrl());
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.gameCover);
        }

        holder.gameTitle.setText(game.getTitle());
        holder.gamePrice.setText(String.format(Locale.getDefault(), "%.2f ₽", game.getPrice()));

        if (showFullInfo) {
            holder.gameGenre.setVisibility(View.VISIBLE);
            holder.gameGenre.setText(String.format("%s • %s",
                    game.getDeveloper() != null ? game.getDeveloper() : "Unknown",
                    game.getPlatform() != null ? game.getPlatform() : "PC"));

            holder.gameRating.setVisibility(View.VISIBLE);
            holder.gameRating.setText("4.5");
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.btnFavorite.setVisibility(View.VISIBLE);
            holder.gameTag.setVisibility(View.VISIBLE);

            if (game.getAgeRating() != null && !game.getAgeRating().isEmpty()) {
                holder.gameTag.setText(game.getAgeRating());
            } else {
                holder.gameTag.setText("New");
            }
        } else {
            holder.gameGenre.setVisibility(View.GONE);
            holder.gameRating.setVisibility(View.GONE);
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.btnFavorite.setVisibility(View.GONE);
            holder.gameTag.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGameClick(game);
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(game);
        });

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) listener.onFavoriteClick(game);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    private String convertToLocalhost(String url) {
        if (url == null) return "";
        return url.replace("localhost", "10.0.2.2")
                .replace("0.0.0.0", "10.0.2.2");
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView gameCover;
        TextView gameTitle, gameGenre, gamePrice, gameRating;
        MaterialButton btnAddToCart;
        ImageButton btnFavorite;
        com.google.android.material.chip.Chip gameTag;
        MaterialCardView gameCard;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameCard = itemView.findViewById(R.id.gameCard);
            gameCover = itemView.findViewById(R.id.gameCover);
            gameTitle = itemView.findViewById(R.id.gameTitle);
            gameGenre = itemView.findViewById(R.id.gameGenre);
            gamePrice = itemView.findViewById(R.id.gamePrice);
            gameRating = itemView.findViewById(R.id.gameRating);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            gameTag = itemView.findViewById(R.id.gameTag);
        }
    }
}
