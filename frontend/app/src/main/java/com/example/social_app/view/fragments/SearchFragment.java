package com.example.social_app.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.FriendRequest;
import com.example.social_app.model.LoiMoiKetBan;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.UserAdapter;
import com.example.social_app.view.adapters.UserSearchAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView searchResultsRecyclerView;
    private UserSearchAdapter userSearchAdapter;
    private UserAdapter userAdapter;
    private List<NguoiDung> userList;
    private String authToken;
    private int currentUserId;
    private EditText edtSearch;
    private boolean isSearching = false;
    private TextView txtMoiNguoi;
    private View layoutSearchHeader;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);

        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        edtSearch = view.findViewById(R.id.edt_search);
        txtMoiNguoi = view.findViewById(R.id.txtMoiNguoi);
        layoutSearchHeader = view.findViewById(R.id.layout_search_header);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");
        currentUserId = sharedPref.getInt("user_id", -1);

        String keyword = getArguments().getString("keyword");
        if (keyword != null) {
            searchResultUsers(keyword);
        }

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
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                    userAdapter = new UserAdapter(userList, getContext());
                    searchResultsRecyclerView.setAdapter(userAdapter);
                    txtMoiNguoi.setVisibility(View.GONE);
                    layoutSearchHeader.setVisibility(View.VISIBLE);
                    userAdapter.notifyDataSetChanged();

                    searchUsers(query);
                } else {
                    isSearching = false;
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    layoutSearchHeader.setVisibility(View.GONE);
                    txtMoiNguoi.setVisibility(View.VISIBLE);

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

        return view;
    }

    private void searchResultUsers(String query) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.searchUsers("Bearer " + authToken, query).enqueue(new Callback<List<NguoiDung>>() {
            @Override
            public void onResponse(Call<List<NguoiDung>> call, Response<List<NguoiDung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    userSearchAdapter = new UserSearchAdapter(getContext(), userList, currentUserId, new UserSearchAdapter.FriendRequestListener() {
                        @Override
                        public void onSendFriendRequest(NguoiDung nguoiDung) {
                            sendFriendRequest(nguoiDung);
                        }
                        @Override
                        public void onCancelFriendRequest(NguoiDung nguoiDung) {
                            cancelFriendRequest(nguoiDung);
                        }
                    });

                    searchResultsRecyclerView.setAdapter(userSearchAdapter);
                    userSearchAdapter.notifyDataSetChanged();
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

    public void sendFriendRequest(NguoiDung nguoiDung) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        FriendRequest request = new FriendRequest(nguoiDung.getMaNguoiDung());

        apiService.sendFriendRequest("Bearer " + authToken, request).enqueue(new Callback<LoiMoiKetBan>() {
            @Override
            public void onResponse(Call<LoiMoiKetBan> call, Response<LoiMoiKetBan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoiMoiKetBan loiMoi = response.body();
                    int maLoiMoi = loiMoi.getMaLoiMoi();

                    String key = "maLoiMoi_" + currentUserId + "_" + nguoiDung.getMaNguoiDung();
                    SharedPreferences preferences = getContext().getSharedPreferences("FriendRequests", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(key, maLoiMoi);
                    editor.apply();

                    userSearchAdapter.updateUserStatus(nguoiDung, 2);
                    Toast.makeText(getContext(), "Đã gửi lời mời đến " + nguoiDung.getTenNguoiDung(), Toast.LENGTH_SHORT).show();
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


    public void cancelFriendRequest(NguoiDung nguoiDung) {
        SharedPreferences preferences = getContext().getSharedPreferences("FriendRequests", Context.MODE_PRIVATE);
        String key = "maLoiMoi_" + currentUserId + "_" + nguoiDung.getMaNguoiDung();
        int maLoiMoi = preferences.getInt(key, -1);

        if (maLoiMoi != -1) {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            apiService.cancelFriendRequest("Bearer " + authToken, maLoiMoi)
                    .enqueue(new Callback<LoiMoiKetBan>() {
                        @Override
                        public void onResponse(Call<LoiMoiKetBan> call, Response<LoiMoiKetBan> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                userSearchAdapter.updateUserStatus(nguoiDung, 4);
                                Toast.makeText(getContext(), "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Không thể hủy lời mời", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoiMoiKetBan> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối khi hủy lời mời", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã lời mời", Toast.LENGTH_SHORT).show();
        }
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
}