package com.example.newsapp;

public class Constants {

    // ============================================================
    // TODO: Replace with your own FREE API Key from newsapi.org
    // Steps to get free API key:
    // 1. Go to https://newsapi.org/register
    // 2. Sign up for FREE (no credit card required)
    // 3. Copy your API key and paste it below
    // Free plan: 100 requests/day, developer use only
    // ============================================================
    public static final String API_KEY = "4a4c1db6072f46c19e1a7dddac06b3c8";

    // API Settings
    public static final String COUNTRY = "us";
    public static final String LANGUAGE = "en";
    public static final int PAGE_SIZE = 20;
    public static final String SORT_BY = "publishedAt";

    // Intent keys
    public static final String EXTRA_ARTICLE_URL = "article_url";
    public static final String EXTRA_ARTICLE_TITLE = "article_title";
    public static final String EXTRA_ARTICLE_IMAGE = "article_image";
    public static final String EXTRA_ARTICLE_DESC = "article_desc";
    public static final String EXTRA_ARTICLE_CONTENT = "article_content";
    public static final String EXTRA_ARTICLE_SOURCE = "article_source";
    public static final String EXTRA_ARTICLE_DATE = "article_date";
    public static final String EXTRA_ARTICLE_AUTHOR = "article_author";

    // News Categories
    public static final String CAT_GENERAL = "general";
    public static final String CAT_TECHNOLOGY = "technology";
    public static final String CAT_BUSINESS = "business";
    public static final String CAT_SPORTS = "sports";
    public static final String CAT_SCIENCE = "science";
    public static final String CAT_HEALTH = "health";
    public static final String CAT_ENTERTAINMENT = "entertainment";

    // Splash screen delay
    public static final int SPLASH_DELAY = 2500;
}