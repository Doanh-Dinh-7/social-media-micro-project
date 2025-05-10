package com.example.social_app.view.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.fragments.CommentFragment;
import com.example.social_app.view.fragments.ConversationFragment;
import com.example.social_app.view.fragments.CreatePostFragment;
import com.example.social_app.view.fragments.PostFragment;
import com.example.social_app.view.fragments.ProfileFragment;
import com.example.social_app.view.fragments.ThongBaoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PostActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new PostFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Xử lý sự kiện khi chọn mục Home (Trang chủ)
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new PostFragment();
            }
            // Xử lý sự kiện khi chọn mục Create Post
            else if (item.getItemId() == R.id.nav_post) {
                selectedFragment = new CreatePostFragment();
            }
            // Nếu có thêm mục khác thì thêm ở đây
            else if (item.getItemId() == R.id.nav_notification) {
                selectedFragment = new ThongBaoFragment();
                // Xử lý thông báo
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        // Kiểm tra nếu là CreatePostFragment thì ẩn BottomNavigationView
        if (fragment instanceof CreatePostFragment) {
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof CommentFragment || currentFragment instanceof CreatePostFragment || currentFragment instanceof ConversationFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PostFragment())
                    .commit();

            // Cập nhật thanh điều hướng về Home và HIỆN NÓ LÊN
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }



    public void hideBottomNavigationView() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationView() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

}
