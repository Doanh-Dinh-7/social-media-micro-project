package com.example.social_app.view.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.fragments.CreatePostFragment;
import com.example.social_app.view.fragments.LoginFormFragment;
import com.example.social_app.view.fragments.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);

        if (savedInstanceState == null) {
            loadFragment(new PostFragment());
        }

    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void onBack() {loadFragment(new PostFragment());
    }

    public void showPostFragment(String noiDung, int maQuyenRiengTu) {
        PostFragment postFragment = new PostFragment();

        Bundle bundle = new Bundle();
        bundle.putString("noidung", noiDung);
        bundle.putInt("maquyenriengtu", maQuyenRiengTu);
        postFragment.setArguments(bundle);

        loadFragment(postFragment);
    }

    public void goToPost() {
        loadFragment(new PostFragment());
    }
}
