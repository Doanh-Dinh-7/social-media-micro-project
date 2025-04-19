package com.example.social_app.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.CommentRequest;
import com.example.social_app.model.CommentResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CommentFragment extends Fragment {

    private EditText etComment;
    private TextView btnSendComment, tvCommentNumber;
    private RecyclerView rvComments;
    private List<CommentResponse> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private int postId;
    private String authToken;
    private int currentUserId;
    private ApiService apiService;

    public CommentFragment(int postId) {
        this.postId = postId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        etComment = view.findViewById(R.id.etComment);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        tvCommentNumber = view.findViewById(R.id.tvCommentNumber);
        rvComments = view.findViewById(R.id.rvComments);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");
        currentUserId = sharedPref.getInt("user_id", -1);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(commentList, currentUserId, new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onDeleteComment(CommentResponse comment) {
                deleteComment(comment.getMaBinhLuan());
            }
            @Override
            public void onCommentCountChanged(int count) {
                // Cập nhật số lượng bình luận vào TextView
                if (tvCommentNumber != null) {
                    tvCommentNumber.setText(String.valueOf(count));
                }
            }
        });
        rvComments.setAdapter(commentAdapter);

        btnSendComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(getContext(), "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                return;
            }

            postComment(commentText);
        });

        loadComments();

        return view;
    }

    private void loadComments() {
        apiService.getComments("Bearer " + authToken, postId).enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentAdapter.setCommentList(response.body());

                    if (tvCommentNumber != null) {
                        tvCommentNumber.setText(String.valueOf(commentAdapter.getItemCount()));
                    }

                } else {
                    Log.e("COMMENT", "Response code: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải bình luận", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải bình luận", Toast.LENGTH_SHORT).show();
                Log.e("COMMENT", "Load failed: " + t.getMessage());
            }
        });
    }

    private void postComment(String commentText) {
        CommentRequest request = new CommentRequest(commentText, postId);
        apiService.createComment("Bearer " + authToken, postId, request).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Bình luận đã được gửi", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadComments();
                } else {
                    Toast.makeText(getContext(), "Không thể gửi bình luận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi gửi bình luận", Toast.LENGTH_SHORT).show();
                Log.e("COMMENT", "Post failed: " + t.getMessage());
            }
        });
    }

    private void deleteComment(int commentId) {
        apiService.deleteComment("Bearer " + authToken, postId, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xoá bình luận", Toast.LENGTH_SHORT).show();
                    loadComments();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("COMMENT", "Delete failed: " + t.getMessage());
            }
        });
    }
}
