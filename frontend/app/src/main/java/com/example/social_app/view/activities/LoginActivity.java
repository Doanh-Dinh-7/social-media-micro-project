package com.example.social_app.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.social_app.MainActivity;
import com.example.social_app.R;
import com.example.social_app.model.LoginRequest;
import com.example.social_app.model.LoginResponse;
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


        // Mở màn hình chọn phương thức đăng nhập mặc định
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

    // Chuyển đến màn hình nhập tài khoản
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
                    // Kiểm tra nếu access_token tồn tại
                    if (response.body().getAccessToken() != null && !response.body().getAccessToken().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Đóng màn hình đăng nhập
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý trường hợp API trả về mã lỗi
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // In ra lỗi chi tiết
                Log.e("LoginActivity", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

