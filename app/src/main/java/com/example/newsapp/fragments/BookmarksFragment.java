package com.example.newsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.Constants;
import com.example.newsapp.NewsAdapter;
import com.example.newsapp.NewsArticle;
import com.example.newsapp.R;
import com.example.newsapp.activities.NewsDetailActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment implements NewsAdapter.OnArticleClickListener {

    // SharedPreferences key — reuse this key anywhere you save bookmarks
    public static final String PREFS_NAME    = "newsapp_bookmarks";
    public static final String KEY_BOOKMARKS = "bookmarked_articles";

    private RecyclerView recyclerView;
    private NewsAdapter  newsAdapter;
    private TextView     tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        tvEmpty      = view.findViewById(R.id.tv_error); // reuse the error TextView as empty-state

        newsAdapter = new NewsAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload bookmarks every time the tab is shown so additions elsewhere are reflected
        loadBookmarks();
    }

    // ── Public helper: call this from NewsDetailActivity to save a bookmark ──────

    /**
     * Saves an article to bookmarks.
     * Call this from NewsDetailActivity when the user taps the bookmark icon.
     */
    public static void saveBookmark(Context context, NewsArticle article) {
        List<NewsArticle> saved = getBookmarks(context);

        // Avoid duplicates (match by URL)
        for (NewsArticle a : saved) {
            if (a.getUrl() != null && a.getUrl().equals(article.getUrl())) return;
        }

        saved.add(article);
        persist(context, saved);
    }

    /**
     * Removes an article from bookmarks by URL.
     */
    public static void removeBookmark(Context context, String articleUrl) {
        List<NewsArticle> saved = getBookmarks(context);
        saved.removeIf(a -> a.getUrl() != null && a.getUrl().equals(articleUrl));
        persist(context, saved);
    }

    /**
     * Returns true if an article is already bookmarked.
     */
    public static boolean isBookmarked(Context context, String articleUrl) {
        for (NewsArticle a : getBookmarks(context)) {
            if (a.getUrl() != null && a.getUrl().equals(articleUrl)) return true;
        }
        return false;
    }

    // ── Private helpers ──────────────────────────────────────────────────────────

    private void loadBookmarks() {
        List<NewsArticle> saved = getBookmarks(requireContext());
        if (saved.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No saved articles yet.\nTap the bookmark icon on any article to save it.");
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            newsAdapter.setArticles(saved);
        }
    }

    private static List<NewsArticle> getBookmarks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_BOOKMARKS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<NewsArticle>>() {}.getType();
        List<NewsArticle> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    private static void persist(Context context, List<NewsArticle> articles) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BOOKMARKS, new Gson().toJson(articles)).apply();
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
}
