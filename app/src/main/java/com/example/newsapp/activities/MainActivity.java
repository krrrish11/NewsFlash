package com.example.newsapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.Constants;
import com.example.newsapp.NewsAdapter;
import com.example.newsapp.NewsArticle;
import com.example.newsapp.NewsResponse;
import com.example.newsapp.R;
import com.example.newsapp.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvError;
    private ChipGroup chipGroupCategories;

    private String currentCategory = Constants.CAT_GENERAL;
    private String currentSearchQuery = "";
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(true);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupCategoryChips();
        setupSwipeRefresh();

        // Load initial news
        loadNews(Constants.CAT_GENERAL);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tvError = findViewById(R.id.tv_error);
        chipGroupCategories = findViewById(R.id.chip_group_categories);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("NewsFlash");
        }
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);

        // Pagination on scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void setupCategoryChips() {
        String[] categories = {
                Constants.CAT_GENERAL,
                Constants.CAT_TECHNOLOGY,
                Constants.CAT_BUSINESS,
                Constants.CAT_SPORTS,
                Constants.CAT_SCIENCE,
                Constants.CAT_HEALTH,
                Constants.CAT_ENTERTAINMENT
        };

        String[] labels = {
                "General", "Technology", "Business",
                "Sports", "Science", "Health", "Entertainment"
        };

        for (int i = 0; i < categories.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(labels[i]);
            chip.setCheckable(true);
            chip.setTextColor(Color.BLACK);
            chip.setCheckedIconVisible(true);

            // Style the chip
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            chip.setChipStrokeColorResource(R.color.colorPrimary);
            chip.setChipStrokeWidth(2f);

            if (i == 0) chip.setChecked(true); // Default: General

            final String category = categories[i];
            chip.setOnClickListener(v -> {
                isSearching = false;
                currentCategory = category;
                newsAdapter.clearArticles();
                loadNews(category);
            });

            chipGroupCategories.addView(chip);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            newsAdapter.clearArticles();
            if (isSearching && !currentSearchQuery.isEmpty()) {
                searchNews(currentSearchQuery);
            } else {
                loadNews(currentCategory);
            }
        });
    }

    private void loadNews(String category) {
        showLoading(true);
        hideError();

        RetrofitClient.getInstance()
                .getNewsApiService()
                .getTopHeadlines(category, Constants.COUNTRY, Constants.API_KEY, Constants.PAGE_SIZE, 1)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();

                            if ("ok".equals(newsResponse.getStatus())) {
                                List<NewsArticle> articles = newsResponse.getArticles();
                                if (articles != null && !articles.isEmpty()) {
                                    newsAdapter.setArticles(articles);
                                    hideError();
                                } else {
                                    showError("No news found for this category.");
                                }
                            } else {
                                showError("Error: " + newsResponse.getMessage());
                            }
                        } else {
                            if (response.code() == 401) {
                                showError("Invalid API Key! Please check your API key in Constants.java");
                            } else if (response.code() == 429) {
                                showError("API rate limit exceeded. Try again later.");
                            } else {
                                showError("Failed to load news. Code: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        showError("Network error: " + t.getMessage() + "\nPlease check your internet connection.");
                    }
                });
    }

    private void searchNews(String query) {
        showLoading(true);
        hideError();
        isSearching = true;
        currentSearchQuery = query;

        RetrofitClient.getInstance()
                .getNewsApiService()
                .searchNews(query, Constants.LANGUAGE, Constants.SORT_BY, Constants.API_KEY, Constants.PAGE_SIZE, 1)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            if ("ok".equals(newsResponse.getStatus())) {
                                List<NewsArticle> articles = newsResponse.getArticles();
                                if (articles != null && !articles.isEmpty()) {
                                    newsAdapter.setArticles(articles);
                                    hideError();
                                } else {
                                    showError("No results found for: \"" + query + "\"");
                                }
                            } else {
                                showError("Search error: " + newsResponse.getMessage());
                            }
                        } else {
                            showError("Failed to search. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        showLoading(false);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search news...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    newsAdapter.clearArticles();
                    searchNews(query.trim());
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                isSearching = false;
                newsAdapter.clearArticles();
                loadNews(currentCategory);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            newsAdapter.clearArticles();
            if (isSearching) {
                searchNews(currentSearchQuery);
            } else {
                loadNews(currentCategory);
            }
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ARTICLE_TITLE, article.getTitle());
        intent.putExtra(Constants.EXTRA_ARTICLE_DESC, article.getDescription());
        intent.putExtra(Constants.EXTRA_ARTICLE_CONTENT, article.getContent());
        intent.putExtra(Constants.EXTRA_ARTICLE_URL, article.getUrl());
        intent.putExtra(Constants.EXTRA_ARTICLE_IMAGE, article.getUrlToImage());
        intent.putExtra(Constants.EXTRA_ARTICLE_SOURCE, article.getSourceName());
        intent.putExtra(Constants.EXTRA_ARTICLE_DATE, article.getFormattedDate());
        intent.putExtra(Constants.EXTRA_ARTICLE_AUTHOR, article.getAuthor());
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) recyclerView.setVisibility(View.GONE);
        else recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}