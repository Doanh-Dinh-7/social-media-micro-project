package com.example.social_app.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.model.LoginRequest;
import com.example.social_app.model.LoginResponse;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.fragments.LoginFormFragment;
import com.example.social_app.view.fragments.LoginOptionsFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        if (savedInstanceState == null) {
            loadFragment(new LoginOptionsFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void goToLoginForm() {
        loadFragment(new LoginFormFragment());
    }

    public void goToLoginOptions() {
        loadFragment(new LoginOptionsFragment());
    }

    public void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    NguoiDung user = response.body().getUser();

                    if (accessToken != null && !accessToken.isEmpty() && user != null) {
                        int userId = user.getMaNguoiDung();

                        getSharedPreferences("user_data", MODE_PRIVATE).edit()
                                .putString("auth_token", accessToken)
                                .putInt("user_id", userId)
                                .apply();

                        Log.d("DEBUG_LOGIN", "User ID nhận được: " + userId);

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, PostActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

