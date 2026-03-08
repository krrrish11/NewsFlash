package com.example.newsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.models.User;
import com.example.newsapp.network.ApiClient;
import com.example.newsapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    TextView nameTextView, emailTextView, phoneTextView, ageTextView;
    ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.user_name);
        emailTextView = findViewById(R.id.user_email);
        phoneTextView = findViewById(R.id.user_phone);
        ageTextView = findViewById(R.id.user_age);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences prefs = getSharedPreferences("NewsAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            Log.e("ProfileActivity", "userId is null or empty");
            return;
        }

        Log.d("ProfileActivity", "userId: " + userId);

        Call<User> call = apiService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        nameTextView.setText(user.getName());
                        emailTextView.setText(user.getEmail());
                        phoneTextView.setText(user.getPhone());
                        ageTextView.setText(user.getAge());

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userId", user.getId());
                        editor.putString("userName", user.getName());
                        editor.putString("userEmail", user.getEmail());
                        editor.putString("userAge", user.getAge());
                        editor.putString("userPhone", user.getPhone());
                        editor.apply();

                    } else {
                        Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Invalid response: " + response.code());
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ProfileActivity", "Exception in onResponse", e);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "API failure", t);
            }
        });

        logoutBtn.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }
}