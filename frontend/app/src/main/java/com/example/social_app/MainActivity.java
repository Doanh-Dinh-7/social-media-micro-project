package com.example.social_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.PostActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (isLoggedIn()) {
            // Người dùng đã đăng nhập -> Mở PostActivity (hoặc HomeActivity)
            navigateToPostActivity();
        } else {
            // Chưa đăng nhập -> Mở LoginActivity
            navigateToLoginActivity();
        }
    }

    // Kiểm tra trạng thái đăng nhập
    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    // Điều hướng tới PostActivity
    private void navigateToPostActivity() {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
        finish();  // Đóng MainActivity để không quay lại
    }

    // Điều hướng tới LoginActivity
    private void navigateToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Đóng MainActivity để không quay lại
    }
}
