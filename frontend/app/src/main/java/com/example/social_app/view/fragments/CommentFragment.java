package com.example.social_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.CommentRequest;
import com.example.social_app.model.CommentResponse;
import com.example.social_app.model.HinhAnh;
import com.example.social_app.model.PostResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.CommentAdapter;
import com.example.social_app.view.adapters.ImageUrlAdapter;

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
    private String authToken;
    private int currentUserId;
    private ApiService apiService;

    private TextView txtContent;
    private TextView txtSoLuotTim;
    private TextView txtSoBinhLuan;
    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnBack;
    private PostResponse currentPost;
    private int postId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        if (getArguments() != null) {
            postId = getArguments().getInt("postId", -1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getActivity() != null) {
                Window window = getActivity().getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.orange)); // dùng requireContext()
            }
        }

        etComment = view.findViewById(R.id.etComment);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        rvComments = view.findViewById(R.id.rvComments);
        tvCommentNumber = view.findViewById(R.id.tvCommentNumber);
        txtContent = view.findViewById(R.id.txtContent);
        txtSoLuotTim = view.findViewById(R.id.txtSoLuotTim);
        txtSoBinhLuan = view.findViewById(R.id.txtSoBinhLuan);
        btnLike =view.findViewById(R.id.btnLike);
        btnComment = view.findViewById(R.id.btnComment);

        btnComment.setOnClickListener(v -> {
            etComment.requestFocus();

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
            }

            if (postId != -1) {
                loadComments(postId);
            }
        });

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();

            if (getActivity() instanceof PostActivity) {
                ((PostActivity) getActivity()).showBottomNavigationView();
            }
        });

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");
        currentUserId = sharedPref.getInt("user_id", -1);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(requireContext(), commentList, currentUserId, new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onDeleteComment(CommentResponse comment) {
                deleteComment(comment.getMaBinhLuan());
            }

            @Override
            public void onCommentCountChanged(int count) {
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

        if (postId != -1) {
            loadComments(postId);
            loadPostInfo(postId);
        }

        return view;
    }

    private void loadComments(int postId) {
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
        if (postId == -1) {
            Toast.makeText(getContext(), "ID bài viết không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        CommentRequest request = new CommentRequest(commentText, postId);
        apiService.createComment("Bearer " + authToken, postId, request).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Bình luận đã được gửi", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadComments(postId);
                    loadPostInfo(postId);
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
        if (postId == -1) {
            Toast.makeText(getContext(), "ID bài viết không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteComment("Bearer " + authToken, postId, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xoá bình luận", Toast.LENGTH_SHORT).show();
                    loadComments(postId);
                    loadPostInfo(postId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("COMMENT", "Delete failed: " + t.getMessage());
            }
        });
    }

    private void loadPostInfo(int postId) {
        apiService.getPostById("Bearer " + authToken, postId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPost = response.body();

                    txtContent.setText(currentPost.getNoiDung());
                    txtSoLuotTim.setText(String.valueOf(currentPost.getSo_luot_thich()));
                    txtSoBinhLuan.setText(String.valueOf(currentPost.getSo_binh_luan()));

                    List<HinhAnh> hinhAnhList = currentPost.getHinh_anh();
                    if (hinhAnhList != null && !hinhAnhList.isEmpty()) {
                        GridView gridSelectedImages = getView().findViewById(R.id.gridSelectedImages);
                        gridSelectedImages.setVisibility(View.VISIBLE);
                        ImageUrlAdapter imageAdapter = new ImageUrlAdapter(getContext(), hinhAnhList);
                        gridSelectedImages.setAdapter(imageAdapter);
                    } else {
                        GridView gridSelectedImages = getView().findViewById(R.id.gridSelectedImages);
                        if (gridSelectedImages != null) {
                            gridSelectedImages.setVisibility(View.GONE);
                        }
                    }

                    if (currentPost.isDa_thich()) {
                        btnLike.setImageResource(R.mipmap.redheart);
                    } else {
                        btnLike.setImageResource(R.mipmap.heart);
                    }

                    btnLike.setOnClickListener(v -> {
                        if (currentPost.isDa_thich()) {
                            unlikePost(postId);
                        } else {
                            likePost(postId);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không thể tải thông tin bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải bài viết", Toast.LENGTH_SHORT).show();
                Log.e("POST", "Load failed: " + t.getMessage());
            }
        });
    }

    private void likePost(int postId) {
        apiService.likePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    txtSoLuotTim.setText(String.valueOf(Integer.parseInt(txtSoLuotTim.getText().toString()) + 1));
                    btnLike.setImageResource(R.mipmap.redheart);
                    updateLikeStatus(true);
                } else {
                    Toast.makeText(getContext(), "Không thể thích bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi like bài viết", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlikePost(int postId) {
        apiService.unlikePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    int currentLikes = Integer.parseInt(txtSoLuotTim.getText().toString());

                    if (currentLikes > 0) {
                        txtSoLuotTim.setText(String.valueOf(currentLikes - 1));
                    }

                    btnLike.setImageResource(R.mipmap.heart);
                    updateLikeStatus(false);
                } else {
                    Toast.makeText(getContext(), "Không thể bỏ thích bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi bỏ thích bài viết", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateLikeStatus(boolean isLiked) {
        if (currentPost != null) {
            currentPost.setDa_thich(isLiked);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof PostActivity) {
            ((PostActivity) getActivity()).showBottomNavigationView();
        }
    }

}
