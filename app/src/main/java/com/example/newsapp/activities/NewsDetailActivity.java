package com.example.newsapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.newsapp.Constants;
import com.example.newsapp.R;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        setupToolbar();
        populateData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void populateData() {
        // Get data from Intent
        String title = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_TITLE);
        String description = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_DESC);
        String content = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_CONTENT);
        String url = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_URL);
        String imageUrl = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_IMAGE);
        String source = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_SOURCE);
        String date = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_DATE);
        String author = getIntent().getStringExtra(Constants.EXTRA_ARTICLE_AUTHOR);

        // Find Views
        ImageView ivDetailImage = findViewById(R.id.iv_detail_image);
        TextView tvDetailTitle = findViewById(R.id.tv_detail_title);
        TextView tvDetailSource = findViewById(R.id.tv_detail_source);
        TextView tvDetailDate = findViewById(R.id.tv_detail_date);
        TextView tvDetailAuthor = findViewById(R.id.tv_detail_author);
        TextView tvDetailDescription = findViewById(R.id.tv_detail_description);
        TextView tvDetailContent = findViewById(R.id.tv_detail_content);
        Button btnReadFull = findViewById(R.id.btn_read_full);
        Button btnShare = findViewById(R.id.btn_share);

        // Set data
        if (title != null) tvDetailTitle.setText(title);
        if (source != null) tvDetailSource.setText(source);
        if (date != null) tvDetailDate.setText("📅 " + date);

        if (author != null && !author.isEmpty()) {
            tvDetailAuthor.setText("✍️ " + author);
            tvDetailAuthor.setVisibility(View.VISIBLE);
        } else {
            tvDetailAuthor.setVisibility(View.GONE);
        }

        if (description != null && !description.isEmpty()) {
            tvDetailDescription.setText(description);
        } else {
            tvDetailDescription.setText("No description available.");
        }

        // Content may be truncated by API (free plan) — show what's available
        if (content != null && !content.isEmpty()) {
            // Remove the "[+xxxx chars]" suffix from NewsAPI truncated content
            String cleanContent = content.replaceAll("\\[\\+\\d+ chars\\]", "").trim();
            if (!cleanContent.isEmpty()) {
                tvDetailContent.setText(cleanContent + "\n\n[Read full article below]");
            } else {
                tvDetailContent.setText(description);
            }
        } else {
            tvDetailContent.setText("Full content not available. Click 'Read Full Article' to view.");
        }

        // Load image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_news)
                    .error(R.drawable.placeholder_news)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setImageResource(R.drawable.placeholder_news);
        }

        // Read Full Article Button
        btnReadFull.setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        // Share Button
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + url);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}