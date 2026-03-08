package com.example.newsapp;

import com.google.gson.annotations.SerializedName;

public class NewsArticle {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    @SerializedName("urlToImage")
    private String urlToImage;

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("source")
    private Source source;

    @SerializedName("author")
    private String author;

    // Inner Source class
    public static class Source {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getUrl() { return url; }
    public String getUrlToImage() { return urlToImage; }
    public String getPublishedAt() { return publishedAt; }
    public Source getSource() { return source; }
    public String getAuthor() { return author; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setContent(String content) { this.content = content; }
    public void setUrl(String url) { this.url = url; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    public void setSource(Source source) { this.source = source; }
    public void setAuthor(String author) { this.author = author; }

    // Helper to get formatted date
    public String getFormattedDate() {
        if (publishedAt != null && publishedAt.length() >= 10) {
            return publishedAt.substring(0, 10);
        }
        return "";
    }

    // Helper to get source name safely
    public String getSourceName() {
        if (source != null && source.getName() != null) {
            return source.getName();
        }
        return "Unknown Source";
    }
}