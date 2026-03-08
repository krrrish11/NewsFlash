package com.example.newsapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newsapp.R;

import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String PREFS_PROFILE = "newsapp_profile";
    private static final String KEY_USERNAME  = "username";

    private TextView tvUsername;
    private TextView tvBookmarkCount;
    private TextView tvAppVersion;
    private Button   btnClearBookmarks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername        = view.findViewById(R.id.tv_username);
        tvBookmarkCount   = view.findViewById(R.id.tv_bookmark_count);
        tvAppVersion      = view.findViewById(R.id.tv_app_version);
        btnClearBookmarks = view.findViewById(R.id.btn_clear_bookmarks);

        loadProfile();

        btnClearBookmarks.setOnClickListener(v -> clearAllBookmarks());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookmark count when returning to this tab
        updateBookmarkCount();
    }

    private void loadProfile() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);

        String username = prefs.getString(KEY_USERNAME, "NewsFlash Reader");
        tvUsername.setText(username);

        try {
            String versionName = requireContext()
                    .getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0)
                    .versionName;
            tvAppVersion.setText("Version " + versionName);
        } catch (Exception e) {
            tvAppVersion.setText("Version 1.0");
        }

        updateBookmarkCount();
    }

    private void updateBookmarkCount() {
        // Reuse the bookmark list size from BookmarksFragment helper
        int count = getBookmarkCount();
        tvBookmarkCount.setText(count + " saved article" + (count == 1 ? "" : "s"));
    }

    private int getBookmarkCount() {
        try {
            SharedPreferences prefs = requireContext()
                    .getSharedPreferences(BookmarksFragment.PREFS_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(BookmarksFragment.KEY_BOOKMARKS, null);
            if (json == null) return 0;

            com.google.gson.reflect.TypeToken<List<com.example.newsapp.NewsArticle>> token =
                    new com.google.gson.reflect.TypeToken<List<com.example.newsapp.NewsArticle>>() {};
            List<?> list = new com.google.gson.Gson().fromJson(json, token.getType());
            return list != null ? list.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void clearAllBookmarks() {
        requireContext()
                .getSharedPreferences(BookmarksFragment.PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(BookmarksFragment.KEY_BOOKMARKS)
                .apply();

        updateBookmarkCount();
        Toast.makeText(requireContext(), "All bookmarks cleared.", Toast.LENGTH_SHORT).show();
    }
}
