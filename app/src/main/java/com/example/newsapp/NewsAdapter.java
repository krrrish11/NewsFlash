package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private List<NewsArticle> articleList;
    private OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(NewsArticle article);
    }

    public NewsAdapter(Context context, OnArticleClickListener listener) {
        this.context = context;
        this.articleList = new ArrayList<>();
        this.listener = listener;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articleList = articles;
        notifyDataSetChanged();
    }

    public void addArticles(List<NewsArticle> articles) {
        int startPos = this.articleList.size();
        this.articleList.addAll(articles);
        notifyItemRangeInserted(startPos, articles.size());
    }

    public void clearArticles() {
        this.articleList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle article = articleList.get(position);

        // Set title
        if (article.getTitle() != null && !article.getTitle().isEmpty()) {
            holder.tvTitle.setText(article.getTitle());
        } else {
            holder.tvTitle.setText("No Title Available");
        }

        // Set description
        if (article.getDescription() != null && !article.getDescription().isEmpty()) {
            holder.tvDescription.setText(article.getDescription());
        } else {
            holder.tvDescription.setText("No description available.");
        }

        // Set source name
        holder.tvSource.setText(article.getSourceName());

        // Set date
        holder.tvDate.setText(article.getFormattedDate());

        // Load image with Glide
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .placeholder(R.drawable.placeholder_news)
                    .error(R.drawable.placeholder_news)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.ivNewsImage);
        } else {
            holder.ivNewsImage.setImageResource(R.drawable.placeholder_news);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArticleClick(article);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewsImage;
        TextView tvTitle, tvDescription, tvSource, tvDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvSource = itemView.findViewById(R.id.tv_source);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}