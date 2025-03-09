package com.example.social_app;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.social_app.model.BaiViet;
import com.example.social_app.viewmodel.PostViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        PostViewModel postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        postViewModel.getPosts().observe(this, posts -> {
            if (posts != null) {
                for (BaiViet post : posts) {
                    Log.d("Post", post.getNoiDung());
                }
            }
        });
    }

}