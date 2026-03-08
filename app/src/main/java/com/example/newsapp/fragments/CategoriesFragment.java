package com.example.newsapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.Constants;
import com.example.newsapp.NewsAdapter;
import com.example.newsapp.NewsArticle;
import com.example.newsapp.NewsResponse;
import com.example.newsapp.R;
import com.example.newsapp.RetrofitClient;
import com.example.newsapp.activities.NewsDetailActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment implements NewsAdapter.OnArticleClickListener {

    private RecyclerView      recyclerView;
    private NewsAdapter       newsAdapter;
    private ProgressBar       progressBar;
    private TextView          tvError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup         chipGroup;

    private String currentCategory = Constants.CAT_TECHNOLOGY; // default tab

    private static final String[] CATEGORIES = {
            Constants.CAT_TECHNOLOGY,
            Constants.CAT_BUSINESS,
            Constants.CAT_SPORTS,
            Constants.CAT_SCIENCE,
            Constants.CAT_HEALTH,
            Constants.CAT_ENTERTAINMENT
    };

    private static final String[] LABELS = {
            "Technology", "Business", "Sports",
            "Science", "Health", "Entertainment"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView       = view.findViewById(R.id.recycler_view);
        progressBar        = view.findViewById(R.id.progress_bar);
        tvError            = view.findViewById(R.id.tv_error);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        chipGroup          = view.findViewById(R.id.chip_group_categories);

        newsAdapter = new NewsAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            newsAdapter.clearArticles();
            loadCategory(currentCategory);
        });

        buildChips();
        loadCategory(currentCategory);
    }

    private void buildChips() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            Chip chip = new Chip(requireContext());
            chip.setText(LABELS[i]);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
            chip.setChipStrokeColorResource(R.color.colorPrimary);
            chip.setChipStrokeWidth(2f);

            if (i == 0) chip.setChecked(true);

            final String category = CATEGORIES[i];
            chip.setOnClickListener(v -> {
                currentCategory = category;
                newsAdapter.clearArticles();
                loadCategory(category);
            });

            chipGroup.addView(chip);
        }
    }

    private void loadCategory(String category) {
        showLoading(true);
        hideError();

        RetrofitClient.getInstance()
                .getNewsApiService()
                .getTopHeadlines(
                        category,
                        Constants.COUNTRY,
                        Constants.API_KEY,
                        Constants.PAGE_SIZE,
                        1
                )
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call,
                                           @NonNull Response<NewsResponse> response) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null
                                && "ok".equals(response.body().getStatus())) {

                            List<NewsArticle> articles = response.body().getArticles();
                            if (articles != null && !articles.isEmpty()) {
                                newsAdapter.setArticles(articles);
                            } else {
                                showError("No articles found for this category.");
                            }
                        } else {
                            showError("Failed to load. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    @Override
    public void onArticleClick(NewsArticle article) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ARTICLE_TITLE,   article.getTitle());
        intent.putExtra(Constants.EXTRA_ARTICLE_DESC,    article.getDescription());
        intent.putExtra(Constants.EXTRA_ARTICLE_CONTENT, article.getContent());
        intent.putExtra(Constants.EXTRA_ARTICLE_URL,     article.getUrl());
        intent.putExtra(Constants.EXTRA_ARTICLE_IMAGE,   article.getUrlToImage());
        intent.putExtra(Constants.EXTRA_ARTICLE_SOURCE,  article.getSourceName());
        intent.putExtra(Constants.EXTRA_ARTICLE_DATE,    article.getFormattedDate());
        intent.putExtra(Constants.EXTRA_ARTICLE_AUTHOR,  article.getAuthor());
        startActivity(intent);
        requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
}
