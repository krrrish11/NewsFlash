package com.example.newsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.models.User;
import com.example.newsapp.network.ApiClient;
import com.example.newsapp.network.ApiService;
import com.example.newsapp.network.LoginRequest;
import com.example.newsapp.network.LoginResponse;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail, tilPassword;
    private EditText etEmail, etPassword;
    private Button loginBtn;
    private CheckBox cbRememberMe;
    private ProgressBar progressBar;
    private View layoutForm;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "NewsAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private TextView tvsignup;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToMain();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        tvsignup = findViewById(R.id.signup);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        loginBtn = findViewById(R.id.loginBtn);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        progressBar = findViewById(R.id.progress_bar);
        layoutForm = findViewById(R.id.layout_form);

        loginBtn.setOnClickListener(v -> attemptLogin());
        tvsignup.setOnClickListener(v -> signup());
    }

    private void attemptLogin() {

        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Valid email required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            return;
        }

        showProgress(true);

        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                showProgress(false);

                if (response.isSuccessful() && response.body() != null &&
                        response.body().isSuccess()) {

                    User user = response.body().getUser();

                    if (user != null && user.getId() != null) {

                        sharedPreferences.edit()
                                .putBoolean(KEY_IS_LOGGED_IN, true)
                                .putString(KEY_USER_ID, user.getId())
                                .apply();

                        Toast.makeText(LoginActivity.this,
                                "Welcome " + user.getName(),
                                Toast.LENGTH_SHORT).show();

                        navigateToMain();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this,
                        "Network Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginBtn.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signup(){
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

}