package com.example.social_app.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
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

public class PostFragment extends Fragment implements PostAdapter.OnPostLikeListener, PostAdapter.OnPostCommentListener, PostAdapter.OnPostDeleteListener {

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
    private int currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.orange));
        }

        edtSearch = view.findViewById(R.id.edt_search);
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        layoutSearchHeader = view.findViewById(R.id.layout_search_header);
        btnMessage = view.findViewById(R.id.btn_message);

        btnMessage.setOnClickListener(v -> {
            ((PostActivity) getActivity()).hideBottomNavigationView();
            ConversationFragment conversationFragment = new ConversationFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, conversationFragment)
                    .addToBackStack(null)
                    .commit();
        });

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext(), this, this, this);
        recyclerView.setAdapter(postAdapter);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

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
                    ((PostActivity) requireActivity()).hideBottomNavigationView();
                    searchUsers(query);
                } else {
                    isSearching = false;
                    tabLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    layoutSearchHeader.setVisibility(View.GONE);
                    searchResultsRecyclerView.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                    ((PostActivity) requireActivity()).showBottomNavigationView();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                    SearchFragment userSearchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("keyword", query);
                    userSearchFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, userSearchFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập từ khoá", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });


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

    private void searchUsers(String query) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.searchUsers("Bearer " + authToken, query).enqueue(new Callback<List<NguoiDung>>() {
            @Override
            public void onResponse(Call<List<NguoiDung>> call, Response<List<NguoiDung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NguoiDung> userList = response.body();
                    if (userList.isEmpty()) {
                        Toast.makeText(getContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }

                    userAdapter = new UserAdapter(userList, getContext());

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

    @Override
    public void onDeleteClicked(PostResponse post, int position) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.deletePost("Bearer " + authToken, post.getMaBaiViet()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    postList.remove(position);
                    postAdapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể xóa bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi xóa bài viết", Toast.LENGTH_SHORT).show();
            }
        });
    }

}