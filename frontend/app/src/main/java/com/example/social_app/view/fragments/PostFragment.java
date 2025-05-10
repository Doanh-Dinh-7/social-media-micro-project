package com.example.social_app.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.FriendRequest;
import com.example.social_app.model.LoiMoiKetBan;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.PostResponse;
import com.example.social_app.view.adapters.UserAdapter;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.PostAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment implements PostAdapter.OnPostLikeListener, PostAdapter.OnPostCommentListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView, searchResultsRecyclerView;
    private List<PostResponse> postList;
    private PostAdapter postAdapter;
    private UserAdapter userAdapter;
    private String authToken;
    private String selectedTopic = "Tất cả";
    private EditText edtSearch;
    private LinearLayout layoutSearchHeader;
    private boolean isSearching = false;
    private ImageView btnMessage;
    private List<LoiMoiKetBan> friendRequests;
    private int currentUserId = 1; // Giả sử ID của người dùng hiện tại là 1

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.orange));
        }

        // Bind views
        edtSearch = view.findViewById(R.id.edt_search);
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        layoutSearchHeader = view.findViewById(R.id.layout_search_header);
        btnMessage = view.findViewById(R.id.btn_message);

        // Set up the message button
        btnMessage.setOnClickListener(v -> {
            ((PostActivity) getActivity()).hideBottomNavigationView();
            ConversationFragment conversationFragment = new ConversationFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, conversationFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Get auth token from SharedPreferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        // Set up main RecyclerView for posts
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext(), this, this);
        recyclerView.setAdapter(postAdapter);

        // Set up search results RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load friend requests
        loadFriendRequests();

        // Load posts
        loadPosts();

        // Search listener for EditText
        edtSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ((PostActivity) requireActivity()).hideBottomNavigationView();
            } else {
                ((PostActivity) requireActivity()).showBottomNavigationView();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    isSearching = true;
                    tabLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    layoutSearchHeader.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    searchUsers(query, false); // false: không hiển thị nút kết bạn trong trang tìm kiếm
                } else {
                    isSearching = false;
                    tabLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutSearchHeader.setVisibility(View.GONE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                    String query = edtSearch.getText().toString().trim();
                    if (!query.isEmpty()) {
                        isSearching = true;
                        tabLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        layoutSearchHeader.setVisibility(View.VISIBLE);
                        searchResultsRecyclerView.setVisibility(View.VISIBLE);
                        searchUsers(query, false);
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                    }

                    // Ẩn bàn phím
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                    return true; // Đã xử lý sự kiện
                }
                return false;
            }
        });



        // Set up tabs
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
            tabLayout.addTab(tabLayout.newTab().setText("Kinh nghiệm"));
            tabLayout.addTab(tabLayout.newTab().setText("Đời sống"));
            tabLayout.addTab(tabLayout.newTab().setText("Hỏi đáp"));
            tabLayout.addTab(tabLayout.newTab().setText("Khác"));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTopic = tab.getText().toString();
                loadPosts();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void loadPosts() {
        if (isSearching) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getAllPosts("Bearer " + authToken).enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostResponse> allPosts = response.body();
                    postList.clear();

                    if (selectedTopic.equals("Tất cả")) {
                        postList.addAll(allPosts);
                    } else {
                        int topicId = getTopicId(selectedTopic);
                        for (PostResponse post : allPosts) {
                            if (post.getMaChuDe() == topicId) {
                                postList.add(post);
                            }
                        }
                    }

                    postAdapter.notifyDataSetChanged();
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

    private void loadFriendRequests() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getFriendRequest("Bearer " + authToken).enqueue(new Callback<List<LoiMoiKetBan>>() {
            @Override
            public void onResponse(Call<List<LoiMoiKetBan>> call, Response<List<LoiMoiKetBan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendRequests = response.body();
                } else {
                    Toast.makeText(getContext(), "Không thể lấy danh sách lời mời", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LoiMoiKetBan>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi tải lời mời", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUsers(String query, boolean showFriendButton) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.searchUsers("Bearer " + authToken, query).enqueue(new Callback<List<NguoiDung>>() {
            @Override
            public void onResponse(Call<List<NguoiDung>> call, Response<List<NguoiDung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NguoiDung> userList = response.body();
                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }

                    userAdapter = new UserAdapter(userList, showFriendButton, getContext(), new UserAdapter.OnItemClickListener() {
                        @Override
                        public void onOpenProfile(NguoiDung nguoiDung) {
                            openUserProfile(nguoiDung);
                        }

                        @Override
                        public void onSendFriendRequest(NguoiDung nguoiDung) {
                            onSendFriend(nguoiDung);
                        }

                        @Override
                        public void onCancelFriendRequest(NguoiDung nguoiDung) {
                            Toast.makeText(getContext(), "Đã hủy lời mời đến: " + nguoiDung.getTenNguoiDung(), Toast.LENGTH_SHORT).show();
                            // TODO: Gọi API hủy lời mời kết bạn nếu có
                        }
                    }, friendRequests, currentUserId);

                    searchResultsRecyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tìm kiếm người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NguoiDung>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onSendFriend(NguoiDung nguoiDung) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        FriendRequest request = new FriendRequest(nguoiDung.getMaNguoiDung());

        apiService.sendFriendRequest("Bearer " + authToken, request)
                .enqueue(new Callback<LoiMoiKetBan>() {
                    @Override
                    public void onResponse(Call<LoiMoiKetBan> call, Response<LoiMoiKetBan> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getContext(), "Đã gửi lời mời đến " + nguoiDung.getTenNguoiDung(), Toast.LENGTH_SHORT).show();
                            userAdapter.updateFriendRequests(friendRequests);
                        } else {
                            Toast.makeText(getContext(), "Không thể gửi lời mời", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoiMoiKetBan> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối khi gửi lời mời", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openUserProfile(NguoiDung nguoiDung) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", nguoiDung.getMaNguoiDung());
        profileFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit();
    }

    private int getTopicId(String topicName) {
        switch (topicName) {
            case "Kinh nghiệm": return 1;
            case "Đời sống": return 2;
            case "Hỏi đáp": return 3;
            case "Khác": return 4;
            default: return -1;
        }
    }

    @Override
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
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.likePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void unlikePost(int postId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.unlikePost("Bearer " + authToken, postId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    @Override
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