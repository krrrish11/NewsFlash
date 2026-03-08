package com.example.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.network.ApiClient;
import com.example.newsapp.network.ApiService;
import com.example.newsapp.network.SignupRequest;
import com.example.newsapp.network.UserResponse;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    
    // UI Components
    private TextInputLayout tilName, tilPhone, tilEmail, tilAge, tilPassword, tilConfirmPassword;
    private EditText etName, etPhone, etEmail, etAge, etPassword, etConfirmPassword;
    private Button signupBtn;
    private TextView alreadyAccountText;
    private CheckBox cbTerms;
    private ProgressBar progressBar;
    private View layoutLogo, layoutForm;

    // API Service
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Hide action bar if exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Retrofit API
        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        setupAnimations();
        setupListeners();
    }

    private void initViews() {
        layoutLogo = findViewById(R.id.layout_logo);
        layoutForm = findViewById(R.id.layout_form);
        
        tilName = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);
        tilAge = findViewById(R.id.til_age);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        signupBtn = findViewById(R.id.signupBtn);
        alreadyAccountText = findViewById(R.id.alreadyAccountText);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupAnimations() {
        // Fade in animation for logo
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1000);
        layoutLogo.startAnimation(fadeIn);

        // Slide up animation for form
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        layoutForm.startAnimation(slideUp);
    }

    private void setupListeners() {
        // Signup button click
        signupBtn.setOnClickListener(view -> attemptSignup());

        // Already have account - navigate to login
        alreadyAccountText.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Clear errors on text change
        etName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilName.setError(null);
            }
        });

        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPhone.setError(null);
            }
        });

        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
        });

        etAge.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilAge.setError(null);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
                tilConfirmPassword.setError(null);
            }
        });

        etConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
            }
        });
    }

    private void attemptSignup() {
        // Reset all errors
        tilName.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilAge.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get input values
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name is required");
            isValid = false;
        } else if (name.length() < 3) {
            tilName.setError("Name must be at least 3 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 10) {
            tilPhone.setError("Please enter a valid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            isValid = false;
        }

        if (TextUtils.isEmpty(age)) {
            tilAge.setError("Age is required");
            isValid = false;
        } else {
            try {
                int ageValue = Integer.parseInt(age);
                if (ageValue < 13 || ageValue > 120) {
                    tilAge.setError("Please enter a valid age (13-120)");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilAge.setError("Please enter a valid number");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            tilPassword.setError("Use letters, numbers and special characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept Terms & Conditions", 
                          Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            layoutForm.startAnimation(shake);
            return;
        }

        // Show progress
        showProgress(true);

        // Make API call
        SignupRequest signupRequest = new SignupRequest(name, phone, email, password, age);
        Call<UserResponse> call = apiService.register(signupRequest);
        
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    
                    if (userResponse.isSuccess()) {
                        Log.d(TAG, "Signup successful");
                        Toast.makeText(SignUpActivity.this, 
                            "Account created successfully! Please login.", 
                            Toast.LENGTH_LONG).show();
                        
                        // Navigate back to login
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        String message = userResponse.getMessage();
                        showError(message != null ? message : "Signup failed. Please try again.");
                        Log.e(TAG, "Signup not successful: " + message);
                    }
                } else {
                    showError("Signup failed. Email might already exist.");
                    Log.e(TAG, "Response not successful. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showProgress(false);
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "Signup API call failed", t);
                
                // Shake animation on error
                Animation shake = AnimationUtils.loadAnimation(
                    SignUpActivity.this, R.anim.shake);
                layoutForm.startAnimation(shake);
            }
        });
    }

    private boolean isPasswordStrong(String password) {
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        return hasLetter && hasNumber;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        signupBtn.setEnabled(!show);
        signupBtn.setText(show ? "Creating Account..." : "Create Account");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Simple TextWatcher
    private abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }

}
