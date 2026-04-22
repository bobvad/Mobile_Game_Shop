// ApiService.java
package com.example.mobile_gamestoreshop.api;

import com.example.mobile_gamestoreshop.models.Game;
import com.example.mobile_gamestoreshop.models.Purchase;
import com.example.mobile_gamestoreshop.models.Review;
import com.example.mobile_gamestoreshop.models.User;
import com.example.mobile_gamestoreshop.models.UserStats;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/UserController/SingIn")
    Call<User> signIn(
            @Field("Login") String login,
            @Field("Password") String password
    );

    @FormUrlEncoded
    @POST("api/UserController/RegIn")
    Call<User> register(
            @Field("Login") String login,
            @Field("Email") String email,
            @Field("Password") String password,
            @Field("DateTimeCreated") String dateTimeCreated
    );

    @FormUrlEncoded
    @POST("api/UserController/RegInAdmin")
    Call<User> registerAdmin(
            @Field("Login") String login,
            @Field("Email") String email,
            @Field("Password") String password
    );

    @GET("api/UserController/GetStats")
    Call<UserStats> getUserStats(@Query("id") int userId);

    @FormUrlEncoded
    @DELETE("api/UserController/DeleteById")
    Call<ResponseBody> deleteUser(@Field("id") int userId);

    @GET("api/GameController/GetAllGames")
    Call<List<Game>> getAllGames();

    @GET("api/GameController/GetById/{id}")
    Call<Game> getGameById(@Path("id") int gameId);

    @GET("api/GameController/GetUserPurchases/{userId}")
    Call<List<Purchase>> getUserPurchases(@Path("userId") int userId);

    @GET("api/GameController/GetByGameId/{gameId}")
    Call<List<Review>> getGameReviews(@Path("gameId") int gameId);

    // ========== 🔧 ADMIN ENDPOINTS (опционально) ==========

    @FormUrlEncoded
    @POST("api/GameController/AddGame")
    Call<Game> addGame(
            @Field("title") String title,
            @Field("description") String description,
            @Field("price") double price,
            @Field("releaseDate") String releaseDate,
            @Field("developer") String developer,
            @Field("publisher") String publisher,
            @Field("ageRating") String ageRating,
            @Field("platform") String platform,
            @Field("imageUrl") String imageUrl
    );

    @FormUrlEncoded
    @PUT("api/GameController/UpdateGame")
    Call<Game> updateGame(
            @Field("id") int id,
            @Field("title") String title,
            @Field("description") String description,
            @Field("price") double price,
            @Field("releaseDate") String releaseDate,
            @Field("developer") String developer,
            @Field("publisher") String publisher,
            @Field("ageRating") String ageRating,
            @Field("platform") String platform,
            @Field("imageUrl") String imageUrl
    );

    /**
     * Удалить игру по ID (требуется авторизация админа)
     * DELETE /api/GameController/DeleteById?id={id}
     */
    @DELETE("api/GameController/DeleteById")
    Call<ResponseBody> deleteGame(@Query("id") int gameId);
}