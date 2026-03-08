package com.example.newsapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("articles")
    private List<NewsArticle> articles;

    @SerializedName("message")
    private String message;

    // Getters
    public String getStatus() { return status; }
    public int getTotalResults() { return totalResults; }
    public List<NewsArticle> getArticles() { return articles; }
    public String getMessage() { return message; }
}