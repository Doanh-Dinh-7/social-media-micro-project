package com.example.social_app.view.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.fragments.RegisterFragment;
import com.example.social_app.view.fragments.SuccessFragment;
import com.example.social_app.view.fragments.UserInforFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if (savedInstanceState == null) {
            loadFragment(new RegisterFragment());
        }
    }

    // Hàm chuyển đổi giữa các Fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void goToSuccess() { loadFragment(new SuccessFragment());}
    public void goToUserInfor() { loadFragment(new UserInforFragment());}
}