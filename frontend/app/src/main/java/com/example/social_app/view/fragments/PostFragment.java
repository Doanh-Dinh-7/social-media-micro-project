package com.example.social_app.view.fragments;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.ChuDe;
import com.example.social_app.model.PostResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.PostAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostFragment extends Fragment implements PostAdapter.OnPostLikeListener, PostAdapter.OnPostCommentListener {

    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private List<PostResponse> postList;
    private PostAdapter adapter;
    private String authToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        adapter = new PostAdapter(postList, getContext(), this, this); // Truyền listener vào đây
        recyclerView.setAdapter(adapter);

        loadPosts();

        // Ánh xạ TabLayout
        tabLayout = view.findViewById(R.id.tabLayout);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
            tabLayout.addTab(tabLayout.newTab().setText("Kinh nghiệm"));
        }

        // Ánh xạ và xử lý bottom nav
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new PostFragment();
            } else if (item.getItemId() == R.id.nav_post) {
                selectedFragment = new CreatePostFragment();
            }

            if (selectedFragment != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        return view;
    }

    private void loadPosts() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getAllPosts("Bearer " + authToken).enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Lỗi xác thực", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi nội dung", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý sự kiện khi nhấn like trong adapter
    @Override
    public void onLikeClicked(PostResponse post, int position) {
        boolean newLikeState = !post.isDa_thich();
        post.setDa_thich(newLikeState);
        post.setSo_luot_thich(post.getSo_luot_thich() + (newLikeState ? 1 : -1));
        adapter.notifyItemChanged(position);

        if (newLikeState) {
            likePost(post.getMaBaiViet());
        } else {
            unlikePost(post.getMaBaiViet());
        }
    }

    private void likePost(int postId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.likePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("LikePost", "Không thể like: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LikePost", "Lỗi khi like", t);
            }
        });
    }

    private void unlikePost(int postId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.unlikePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("UnlikePost", "Không thể unlike: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UnlikePost", "Lỗi khi unlike", t);
            }
        });
    }

    public void onCommentClicked(PostResponse post, int position) {
        // Chuyển sang CommentFragment ở đây
        Fragment commentFragment = new CommentFragment(post.getMaBaiViet());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, commentFragment)
                .addToBackStack(null)
                .commit();
    }

}

