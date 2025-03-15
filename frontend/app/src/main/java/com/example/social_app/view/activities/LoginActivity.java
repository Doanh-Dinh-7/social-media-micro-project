package com.example.social_app.view.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.fragments.LoginFormFragment;
import com.example.social_app.view.fragments.LoginOptionsFragment;
import com.example.social_app.view.fragments.RegisterFragment;


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

}