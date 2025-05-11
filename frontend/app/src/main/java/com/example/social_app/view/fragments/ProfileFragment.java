package com.example.social_app.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.FriendResponse;
import com.example.social_app.model.HinhAnh;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.PostResponse;
import com.example.social_app.model.UserInfoResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.ImageProfileAdapter;
import com.example.social_app.view.adapters.PostAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView txtName;
    private TextView txtLuotTheoDoi;
    private ApiService apiService;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<PostResponse> postList = new ArrayList<>();
    private String authToken;
    private TextView txtPostCount;
    private List<HinhAnh> imageUrls = new ArrayList<>();
    private ImageView btnEditCover;
    private ImageView btnEditProfilePic;
    private int profileUserId = -1;
    private ImageView imgAvatar;
    private ImageView imgCover;
    private LinearLayout btnFriendList;
    private TextView txtSoBanBe;
    private List<FriendResponse> friendList = new ArrayList<>();
    private ImageProfileAdapter adapter;

    private ActivityResultLauncher<Intent> profilePicLauncher;
    private ActivityResultLauncher<Intent> coverPicLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txtName);
        txtLuotTheoDoi = view.findViewById(R.id.txtLuotTheoDoi);
        txtPostCount = view.findViewById(R.id.txtPostCount);
        btnEditCover = view.findViewById(R.id.btn_edit_cover);
        btnEditProfilePic = view.findViewById(R.id.btn_edit_profile_pic);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        imgCover = view.findViewById(R.id.imgCover);
        btnFriendList = view.findViewById(R.id.btnFriendList);
        txtSoBanBe = view.findViewById(R.id.txtSoBanBe);

        profilePicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    updateProfilePicture(selectedImageUri);
                }
            }
        });

        coverPicLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    updateCoverPicture(selectedImageUri);
                }
            }
        });

        btnEditCover.setOnClickListener(v -> {
            // Open Intent to pick cover image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            coverPicLauncher.launch(intent);  // Use the coverPicLauncher
        });

        btnEditProfilePic.setOnClickListener(v -> {
            // Open Intent to pick profile picture
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            profilePicLauncher.launch(intent);  // Use the profilePicLauncher
        });

        // Get auth token from SharedPreferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        postAdapter = new PostAdapter(postList, getContext(), this::onLikeClicked, this::onCommentClicked);
        recyclerViewPosts.setAdapter(postAdapter);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("userId")) {
            profileUserId = bundle.getInt("userId", -1);
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            profileUserId = preferences.getInt("user_id", -1);
        }

        if (profileUserId != -1 && !authToken.isEmpty()) {
            getUserInfo(profileUserId, authToken);
            getAllPosts(authToken, profileUserId);
        }

        btnFriendList.setOnClickListener(v -> {
            Bundle friendBundle = new Bundle();
            friendBundle.putInt("userId", profileUserId);
            FriendListFragment friendListFragment = new FriendListFragment();
            friendListFragment.setArguments(friendBundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, friendListFragment);
            transaction.commit();
        });



        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView recyclerEvents = view.findViewById(R.id.recycler_view_events);
        RecyclerView recyclerViewImages = view.findViewById(R.id.recycler_view_images);
        recyclerViewImages.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ImageProfileAdapter(requireContext(), imageUrls);
        recyclerViewImages.setAdapter(adapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        recyclerViewPosts.setVisibility(View.VISIBLE);
                        recyclerEvents.setVisibility(View.GONE);
                        recyclerViewImages.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerViewPosts.setVisibility(View.GONE);
                        recyclerEvents.setVisibility(View.VISIBLE);
                        recyclerViewImages.setVisibility(View.GONE);
                        break;
                    case 2:
                        recyclerViewPosts.setVisibility(View.GONE);
                        recyclerEvents.setVisibility(View.GONE);
                        recyclerViewImages.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        return view;
    }

    private void updateProfilePicture(Uri selectedImageUri) {
        if (selectedImageUri != null) {
            File profileImageFile = getFileFromUri(selectedImageUri);
            if (profileImageFile.exists()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), profileImageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", profileImageFile.getName(), requestBody);

                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Call<NguoiDung> call = apiService.updateProfilePicture("Bearer " + authToken, body);

                call.enqueue(new Callback<NguoiDung>() {
                    @Override
                    public void onResponse(Call<NguoiDung> call, Response<NguoiDung> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            NguoiDung updatedUser = response.body();
                            String newAvatarUrl = updatedUser.getAnhDaiDien();

                            Glide.with(getContext())
                                    .load(newAvatarUrl)
                                    .into(imgAvatar);

                            Toast.makeText(getActivity(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Cập nhật ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NguoiDung> call, Throwable t) {
                        Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Không có ảnh được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCoverPicture(Uri selectedImageUri) {
        if (selectedImageUri != null) {
            File coverImageFile = getFileFromUri(selectedImageUri);
            if (coverImageFile.exists()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), coverImageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", coverImageFile.getName(), requestBody);

                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Call<NguoiDung> call = apiService.updateCoverPicture("Bearer " + authToken, body);

                call.enqueue(new Callback<NguoiDung>() {
                    @Override
                    public void onResponse(Call<NguoiDung> call, Response<NguoiDung> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            NguoiDung updatedUser = response.body();
                            String newCoverUrl = updatedUser.getAnhBia();

                            Glide.with(getContext())
                                    .load(newCoverUrl)
                                    .into(imgCover);

                            Toast.makeText(getActivity(), "Cập nhật ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Cập nhật ảnh bìa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NguoiDung> call, Throwable t) {
                        Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Không có ảnh được chọn", Toast.LENGTH_SHORT).show();
        }
    }


    private File getFileFromUri(Uri uri) {
        try {
            if (uri == null) return null;
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            File file = new File(getContext().getCacheDir(), "upload_temp_" + System.currentTimeMillis());
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    private void getUserInfo(int userId, String token) {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<UserInfoResponse> call = apiService.getUserInfo("Bearer " + token, userId);

        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String username = response.body().getTenNguoiDung();
                    int luotTheoDoi = response.body().getTheoDoi();
                    txtName.setText(username);
                    txtLuotTheoDoi.setText(String.valueOf(luotTheoDoi));
                    txtSoBanBe.setText(String.valueOf(luotTheoDoi));

                    getFriendList(userId);

                    String avatarUrl = response.body().getAnhDaiDien();

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(avatarUrl)
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.mipmap.user_img);
                    }


                    String AnhBiaUrl = response.body().getAnhBia();

                    if (AnhBiaUrl != null && !AnhBiaUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(AnhBiaUrl)
                                .into(imgCover);
                    } else {
                        imgCover.setImageResource(R.mipmap.anhbia);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                txtName.setText("Connection error");
            }
        });
    }

    private void getFriendList(int userId) {
        String authHeader = "Bearer " + authToken;
        apiService.getDanhSachBanBe(authHeader, userId).enqueue(new Callback<List<FriendResponse>>() {
            @Override
            public void onResponse(Call<List<FriendResponse>> call, Response<List<FriendResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendList.clear();
                    friendList.addAll(response.body());
                    txtSoBanBe.setText(String.valueOf(friendList.size()));
                } else {
                    Toast.makeText(getContext(), "Tải danh sách bạn bè thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FriendResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllPosts(String token, int userId) {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<PostResponse>> call = apiService.getAllPosts("Bearer " + token);

        call.enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostResponse> allPosts = response.body();
                    List<PostResponse> userPosts = new ArrayList<>();
                    imageUrls.clear();

                    for (PostResponse post : allPosts) {
                        if (post.getNguoi_dung().getMaNguoiDung() == userId) {
                            userPosts.add(post);
                            if (post.getHinh_anh() != null) {
                                imageUrls.addAll(post.getHinh_anh());
                            }
                        }
                    }

                    postList.clear();
                    postList.addAll(userPosts);
                    postAdapter.notifyDataSetChanged();

                    txtPostCount.setText(String.valueOf(userPosts.size()));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Connection error while fetching posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLikeClicked(PostResponse post, int position) {
        boolean newLikeState = !post.isDa_thich();
        post.setDa_thich(newLikeState);
        post.setSo_luot_thich(post.getSo_luot_thich() + (newLikeState ? 1 : -1));
        postAdapter.notifyItemChanged(position);

        if (newLikeState) {
            likePost(post.getMaBaiViet());
        } else {
            unlikePost(post.getMaBaiViet());
        }
    }

    private void likePost(int postId) {
        if (!authToken.isEmpty()) {
            apiService = RetrofitClient.getClient().create(ApiService.class);
            apiService.likePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e("LikePost", "Cannot like: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("LikePost", "Error when liking", t);
                }
            });
        }
    }

    private void unlikePost(int postId) {
        if (!authToken.isEmpty()) {
            apiService = RetrofitClient.getClient().create(ApiService.class);
            apiService.unlikePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e("UnlikePost", "Cannot unlike: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("UnlikePost", "Error when unliking", t);
                }
            });
        }
    }

    public void onCommentClicked(PostResponse post, int position) {
        ((PostActivity) getActivity()).hideBottomNavigationView();

        CommentFragment commentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("postId", post.getMaBaiViet());
        commentFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, commentFragment)
                .addToBackStack(null)
                .commit();
    }
}
