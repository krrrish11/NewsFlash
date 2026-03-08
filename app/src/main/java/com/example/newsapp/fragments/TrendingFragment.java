package com.example.newsapp.fragments;

import android.content.Intent;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingFragment extends Fragment implements NewsAdapter.OnArticleClickListener {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;

    // "popularity" sorts by how often the article has been shared/liked/commented on
    private static final String SORT_POPULARITY = "popularity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView       = view.findViewById(R.id.recycler_view);
        progressBar        = view.findViewById(R.id.progress_bar);
        tvError            = view.findViewById(R.id.tv_error);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        newsAdapter = new NewsAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            newsAdapter.clearArticles();
            loadTrending();
        });

        loadTrending();
    }

    /**
     * Uses the /v2/everything endpoint with sortBy=popularity to fetch trending stories.
     * A broad query "a" is used because /everything requires at least one search param.
     */
    private void loadTrending() {
        showLoading(true);
        hideError();

        RetrofitClient.getInstance()
                .getNewsApiService()
                .searchNews(
                        "a",                    // broad query required by the endpoint
                        Constants.LANGUAGE,
                        SORT_POPULARITY,
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
                                showError("No trending articles found.");
                            }
                        } else {
                            showError("Failed to load trending news. Code: " + response.code());
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
