package com.example.newsapp.network;

import com.example.newsapp.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    
    // Login endpoint
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    // Register/Signup endpoint
    @POST("register")
    Call<UserResponse> register(@Body SignupRequest signupRequest);

    @GET("/api/user/{id}")
    Call<User> getUserById(@Path("id") String userId);
    
    // Alternative endpoints (adjust based on your API)
    // @POST("users/login")
    // @POST("users/signup")
}
