package com.example.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {

    // Get top headlines by category
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("category") String category,
            @Query("country") String country,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize,
            @Query("page") int page
    );

    // Search news by keyword
    @GET("v2/everything")
    Call<NewsResponse> searchNews(
            @Query("q") String query,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize,
            @Query("page") int page
    );

    // Get top headlines (no category filter)
    @GET("v2/top-headlines")
    Call<NewsResponse> getLatestNews(
            @Query("country") String country,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize
    );
}